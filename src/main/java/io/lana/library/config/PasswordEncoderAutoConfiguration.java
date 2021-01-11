package io.lana.library.config;

import at.favre.lib.crypto.bcrypt.BCrypt;
import io.lana.library.core.spi.PasswordEncoder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(PasswordEncoder.class)
public class PasswordEncoderAutoConfiguration {

    public static class BcryptPasswordEncoder implements PasswordEncoder {
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

    @Bean
    @ConditionalOnMissingBean
    public PasswordEncoder passwordEncoder() {
        return new PasswordEncoderAutoConfiguration.BcryptPasswordEncoder();
    }
}
