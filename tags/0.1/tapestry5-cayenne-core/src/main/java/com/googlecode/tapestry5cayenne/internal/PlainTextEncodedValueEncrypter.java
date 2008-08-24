package com.googlecode.tapestry5cayenne.internal;

import com.googlecode.tapestry5cayenne.services.EncodedValueEncrypter;

public class PlainTextEncodedValueEncrypter implements EncodedValueEncrypter {

    public String decrypt(String encryptedValue) {
        return encryptedValue;
    }

    public String encrypt(String plainTextValue) {
        return plainTextValue;
    }

}
