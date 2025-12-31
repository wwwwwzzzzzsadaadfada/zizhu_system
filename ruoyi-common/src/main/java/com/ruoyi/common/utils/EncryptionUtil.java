package com.ruoyi.common.utils;

import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * AES加密解密工具类
 * 用于敏感信息的加密和解密，如身份证号、住址等
 * 
 * @author ruoyi
 * @date 2025-12-30
 */
@Component
public class EncryptionUtil
{
    private static final Logger logger = LoggerFactory.getLogger(EncryptionUtil.class);
    
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
    
    private static String secretKey;
    
    @Value("${encryption.aes-key:ruoyi_student_key}")
    public void setSecretKey(String key)
    {
        secretKey = key;
        logger.info("AES加密密钥已初始化，密钥长度: {}", secretKey != null ? secretKey.length() : 0);
    }
    
    /**
     * AES加密
     * 
     * @param plaintext 明文
     * @return 密文（Base64编码）
     */
    public static String encrypt(String plaintext)
    {
        if (plaintext == null || plaintext.isEmpty())
        {
            return plaintext;
        }
        
        try
        {
            // 密钥长度必须为16字节
            if (secretKey == null || secretKey.length() < 16)
            {
                logger.warn("加密密钥未配置或长度不足(不加密)，密钥: {}", secretKey);
                return plaintext;
            }
            
            logger.debug("开始加密，密钥长度: {}", secretKey.length());
            SecretKeySpec keySpec = new SecretKeySpec(
                secretKey.substring(0, 16).getBytes("UTF-8"), 0, 16, ALGORITHM
            );
            
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            
            byte[] encrypted = cipher.doFinal(plaintext.getBytes("UTF-8"));
            String result = Base64.getEncoder().encodeToString(encrypted);
            logger.debug("加密成功");
            return result;
        }
        catch (Exception e)
        {
            logger.error("加密失败: {}", e.getMessage());
            throw new RuntimeException("加密失败", e);
        }
    }
    
    /**
     * AES解密
     * 
     * @param ciphertext 密文（Base64编码）
     * @return 明文
     */
    public static String decrypt(String ciphertext)
    {
        if (ciphertext == null || ciphertext.isEmpty())
        {
            return ciphertext;
        }
        
        try
        {
            if (secretKey == null || secretKey.length() < 16)
            {
                logger.warn("解密密钥未配置或长度不足(不解密)，密钥: {}", secretKey);
                return ciphertext;
            }
            
            logger.debug("开始解密，密钥长度: {}, 密文长度: {}", secretKey.length(), ciphertext.length());
            SecretKeySpec keySpec = new SecretKeySpec(
                secretKey.substring(0, 16).getBytes("UTF-8"), 0, 16, ALGORITHM
            );
            
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            
            byte[] decrypted = cipher.doFinal(
                Base64.getDecoder().decode(ciphertext)
            );
            
            String result = new String(decrypted, "UTF-8");
            logger.debug("解密成功");
            return result;
        }
        catch (IllegalArgumentException e)
        {
            // Base64 解码失败或数据不是有效的加密数据，返回原文本
            logger.debug("数据并不是有效的加密数据，返回原文本: {}", e.getMessage());
            return ciphertext;
        }
        catch (Exception e)
        {
            // 下青虎解密错误，将数据当作是粗文本返回
            logger.warn("解密失败: {}. 数据并不是有效的加密文本，将返回原文本", e.getMessage());
            return ciphertext;
        }
    }
}
