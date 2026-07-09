package com.example.demo.Security;

import com.example.demo.Entity.Utilisateur;
import com.example.demo.Repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * Service personnalisé pour charger les détails de l'utilisateur pour Spring Security.
 * Dans Likelamba, l'identifiant unique de connexion est le numéro de téléphone.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UtilisateurRepository utilisateurRepository;

    @Override
    public UserDetails loadUserByUsername(String telephone) throws UsernameNotFoundException {
        Utilisateur utilisateur = utilisateurRepository.findByTelephone(telephone)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Utilisateur non trouvé avec le numéro de téléphone : " + telephone
                ));

        // Retourne un UserDetails standard de Spring Security.
        // N'ayant pas encore de rôles complexes au niveau Spring Security (gérés par @RequiresPermission),
        // nous passons une liste d'autorités vide pour le moment.
        return new User(
                utilisateur.getTelephone(),
                utilisateur.getMotDePasseHash(),
                new ArrayList<>()
        );
    }
}
