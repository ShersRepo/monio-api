package com.smart_tiger.monio.middleware.security.authentication;

import com.smart_tiger.monio.middleware.exception.JwtStorageFailureException;
import com.smart_tiger.monio.middleware.exception.JwtTokenRotationException;
import com.smart_tiger.monio.middleware.exception.JwtTokenStoreFailure;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.time.Instant;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicReference;

import static com.smart_tiger.monio.middleware.security.SecurityConstants.JWT_KEY_ALIAS;

@Component
public class JwtKeyManager {
    private static final Logger logger = LoggerFactory.getLogger(JwtKeyManager.class);
    private static final String KEY_STORE_TYPE = "JCEKS";
    private static final int KEY_SIZE_BYTES = 64; // 512 bits
    
    private final KeyStore keyStore;
    private final JwtKeyStoreConfig config;
    private final AtomicReference<String> currentKeyAlias = new AtomicReference<>();
    private final Path keyStorePath;
    
    public JwtKeyManager(JwtKeyStoreConfig config) throws Exception {
        this.config = config;
        this.keyStore = KeyStore.getInstance(KEY_STORE_TYPE);
        this.keyStorePath = Paths.get(config.getStoreLocation());
        
        initializeKeyStore();
    }
    
    private void initializeKeyStore() throws Exception {
        if (Files.exists(keyStorePath)) {
            loadExistingKeyStore();
        } else {
            createNewKeyStore();
        }
        
        // Initialize current key
        String latestKeyAlias = findLatestKeyAlias();
        if (latestKeyAlias == null) {
            rotateKey(); // Generate initial key if no keys exist
        } else {
            currentKeyAlias.set(latestKeyAlias);
        }
    }
    
    private void loadExistingKeyStore() throws IOException, NoSuchAlgorithmException, CertificateException {
        try (InputStream is = Files.newInputStream(keyStorePath)) {
            keyStore.load(is, config.getPassword().toCharArray());
        }
    }
    
    private void createNewKeyStore() throws Exception {
        keyStore.load(null, config.getPassword().toCharArray());
        saveKeyStore();
    }
    
    private void saveKeyStore() throws JwtStorageFailureException {
        try (OutputStream os = Files.newOutputStream(keyStorePath)) {
            keyStore.store(os, config.getPassword().toCharArray());
        } catch (Exception e) {
            throw new JwtStorageFailureException("Failed to storage JWT Key", e);
        }
    }
    
    private String findLatestKeyAlias() throws KeyStoreException {
        Enumeration<String> aliases = keyStore.aliases();
        String latestAlias = null;
        long latestTimestamp = 0;
        
        while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();
            // Assuming alias format: "jwt-key-{timestamp}"
            try {
                long timestamp = Long.parseLong(alias.substring(8));
                if (timestamp > latestTimestamp) {
                    latestTimestamp = timestamp;
                    latestAlias = alias;
                }
            } catch (NumberFormatException e) {
                logger.warn("Invalid key alias format: {}", alias);
            }
        }
        
        return latestAlias;
    }
    
    public SecretKey getCurrentKey() {
        try {
            return (SecretKey) keyStore.getKey(
                currentKeyAlias.get(),
                config.getKeyPassword().toCharArray()
            );
        } catch (Exception e) {
            throw new JwtTokenStoreFailure("Failed to get current key", e);
        }
    }
    
    public String getCurrentKeyAlias() {
        return currentKeyAlias.get();
    }
    
    public SecretKey getKey(String alias) {
        try {
            return (SecretKey) keyStore.getKey(
                alias,
                config.getKeyPassword().toCharArray()
            );
        } catch (Exception e) {
            return null;
        }
    }
    
    @Scheduled(cron = "0 0 0 * * *") // Run at midnight every day
    public synchronized void rotateKey() {
        try {
            String newKeyAlias = JWT_KEY_ALIAS + Instant.now().getEpochSecond();
            SecretKey newKey = generateKey();
            
            // Store the new key
            KeyStore.SecretKeyEntry secretKeyEntry = new KeyStore.SecretKeyEntry(newKey);
            KeyStore.ProtectionParameter protParam = 
                new KeyStore.PasswordProtection(config.getKeyPassword().toCharArray());
            
            keyStore.setEntry(newKeyAlias, secretKeyEntry, protParam);
            
            // Update current key alias
            String oldKeyAlias = currentKeyAlias.get();
            currentKeyAlias.set(newKeyAlias);
            
            // Remove old keys except current and previous
            Enumeration<String> aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                if (!alias.equals(newKeyAlias) && !alias.equals(oldKeyAlias)) {
                    keyStore.deleteEntry(alias);
                }
            }
            
            // Save the updated keystore
            saveKeyStore();
            
        } catch (Exception e) {
            throw new JwtTokenRotationException("Failed to rotate key", e);
        }
    }
    
    private SecretKey generateKey() {
        byte[] keyBytes = new byte[KEY_SIZE_BYTES];
        new SecureRandom().nextBytes(keyBytes);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}