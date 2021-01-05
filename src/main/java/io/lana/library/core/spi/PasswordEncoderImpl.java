package io.lana.library.core.spi;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoderImpl implements PasswordEncoder {
    @Override
    public String encode(CharSequence rawPassword) {
        return BCrypt.withDefaults().hashToString(12, rawPassword.toString().toCharArray());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return BCrypt.verifyer().verify(rawPassword.toString().toCharArray(),
            encodedPassword.toCharArray()).verified;
    }
}
