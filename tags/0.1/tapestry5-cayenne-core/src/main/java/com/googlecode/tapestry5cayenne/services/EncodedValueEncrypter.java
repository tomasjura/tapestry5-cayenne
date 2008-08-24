package com.googlecode.tapestry5cayenne.services;

public interface EncodedValueEncrypter {
    
    String encrypt(String plainTextValue);
    
    String decrypt(String encryptedValue);

}
