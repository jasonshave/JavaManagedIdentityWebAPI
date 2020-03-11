package com.microsoft.javamanagedidentitywebapi.serverprototype;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.proc.BadJOSEException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.MalformedURLException;
import java.text.ParseException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ServerprototypeApplicationTests {

    private final String invalidSignatureToken = "fake_token";

    @Autowired TokenProvider tokenProvider;

    @Test
    void provideInvalidSignatureToken_validate_returnsFalse() throws MalformedURLException, BadJOSEException, ParseException, JOSEException {
        //Arrange
        boolean result = false;

        try {
            result = tokenProvider.validateToken(invalidSignatureToken);
        } catch (ParseException e){

        }

        //Assert
        assertThat(result).isFalse();
    }

    @Test
    void provideInvalidSignatureToken_validate_returnsBadJOSEException(){
        //Assert
        Assertions.assertThrows(ParseException.class, () -> {tokenProvider.validateToken(invalidSignatureToken);});
    }
}
