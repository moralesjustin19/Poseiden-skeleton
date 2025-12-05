package com.nnk.springboot;

import com.nnk.springboot.domain.User;
import com.nnk.springboot.repositories.UserRepository;
import com.nnk.springboot.services.CustomUserDetailsService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Tests unitaires pour le service CustomUserDetailsService
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTests {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testLoadUserByUsername() {
        // Créer un utilisateur de test
        User user = new User();
        user.setUsername("testuser");
        user.setPassword(new BCryptPasswordEncoder().encode("password123"));
        user.setFullname("Test User");
        user.setRole("USER");

        // Sauvegarder l'utilisateur
        userRepository.save(user);

        // Charger l'utilisateur via le service
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        // Vérifications
        Assert.assertNotNull(userDetails);
        Assert.assertEquals("testuser", userDetails.getUsername());
        Assert.assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));

        // Nettoyer
        userRepository.delete(user);
    }

    @Test(expected = UsernameNotFoundException.class)
    public void testLoadUserByUsername_UserNotFound() {
        // Tenter de charger un utilisateur inexistant
        userDetailsService.loadUserByUsername("nonexistent_user_xyz");
    }

    @Test
    public void testPasswordEncoding() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "mypassword";
        String encodedPassword = encoder.encode(rawPassword);

        // Vérifier que le mot de passe est encodé
        Assert.assertNotEquals(rawPassword, encodedPassword);

        // Vérifier que l'encodage fonctionne
        Assert.assertTrue(encoder.matches(rawPassword, encodedPassword));
    }
}

