package com.example.demo.Security;

import com.example.demo.Entity.Utilisateur;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

/**
 * Service pour la gestion des JSON Web Tokens (JWT).
 * Permet de générer, d'extraire les informations et de valider les tokens JWT.
 */
@Service
public class JwtService {

    // Clé secrète de signature (codée en Base64). Doit faire au moins 256 bits.
    // Par défaut, nous utilisons une clé prédéfinie pour le développement.
    @Value("${application.security.jwt.secret-key:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}")
    private String secretKey;

    @Value("${application.security.jwt.expiration:86400000}") // 24 heures par défaut
    private long jwtExpiration;

    @Value("${application.security.jwt.refresh-expiration:604800000}") // 7 jours par défaut
    private long refreshExpiration;

    /**
     * Extrait le numéro de téléphone (subject) du token JWT.
     */
    public String extraireTelephone(String token) {
        return extraireClaim(token, Claims::getSubject);
    }

    public UUID extraireIdUtilisateur(String token) {
        Claims claims = extraireTousClaims(token);
        Object id = claims.get("id_utilisateur");
        if (id instanceof String s) {
            return UUID.fromString(s);
        }
        if (id instanceof UUID uuid) {
            return uuid;
        }
        throw new IllegalArgumentException("id_utilisateur manquant dans le token");
    }

    /**
     * Extrait une information spécifique (Claim) du token JWT.
     */
    public <T> T extraireClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extraireTousClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Génère un token JWT pour un utilisateur donné.
     */
    public String genererToken(Utilisateur utilisateur) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("id_utilisateur", utilisateur.getIdUtilisateur());
        extraClaims.put("nom", utilisateur.getNom());
        return genererToken(extraClaims, utilisateur.getTelephone());
    }

    public String genererRefreshToken(Utilisateur utilisateur) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("id_utilisateur", utilisateur.getIdUtilisateur());
        extraClaims.put("type", "refresh");
        return genererToken(extraClaims, utilisateur.getTelephone(), refreshExpiration);
    }

    public long getJwtExpiration() {
        return jwtExpiration;
    }

    /**
     * Génère un token JWT avec des claims supplémentaires et un sujet (numéro de téléphone).
     */
    public String genererToken(Map<String, Object> extraClaims, String subject) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(obtenirCleSignature(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String genererToken(Map<String, Object> extraClaims, String subject, long expirationMs) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(obtenirCleSignature(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Valide le token JWT par rapport aux détails de l'utilisateur connecté.
     */
    public boolean estTokenValide(String token, UserDetails userDetails) {
        final String telephone = extraireTelephone(token);
        return (telephone.equals(userDetails.getUsername())) && !estTokenExpire(token);
    }

    /**
     * Vérifie si le token JWT a expiré.
     */
    private boolean estTokenExpire(String token) {
        return extraireDateExpiration(token).before(new Date());
    }

    /**
     * Extrait la date d'expiration du token JWT.
     */
    private Date extraireDateExpiration(String token) {
        return extraireClaim(token, Claims::getExpiration);
    }

    /**
     * Extrait tous les claims du token JWT.
     */
    private Claims extraireTousClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(obtenirCleSignature())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Obtient la clé cryptographique pour signer le JWT.
     */
    private Key obtenirCleSignature() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Key extraireCleSignature() {
        return obtenirCleSignature();
    }
}
