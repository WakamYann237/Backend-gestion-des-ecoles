package com.uds.project.service_authentification_compte.configuration;

import java.util.HashMap;
import java.util.Map;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

import javax.crypto.spec.SecretKeySpec;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;


@Component
public class JwtUtils {// classe qui va nous permettre de construire notre token.
    //recuperation des informations de notre jwt.
    @Value("${app.secret-key}")
    private String secretKey;

    @Value("${app.expiration}")
    private Long expirationTime;

    //Generation du token.
    public String generateToken(String username){
        Map<String , Object> claims = new HashMap<>();//va nous permettre de generer le token.
        return createToken(claims, username);
            }
        
            private String createToken(Map<String, Object>claims, String subject) {
               return Jwts.builder() // le builder va nous permettre de construire notre jwt
                                    .setClaims(claims)
                                    .setSubject(subject)
                                    .setIssuedAt(new Date(System.currentTimeMillis()))//DATE ACTUELLE
                                    .setExpiration(new Date(System.currentTimeMillis() + expirationTime))// date d expiration par rapport a la date que j ai
                                    .signWith(getSignKey(),SignatureAlgorithm.HS256)//le token a 3 parties l entete , le prelude et la signature nous definissons la signature maintenant
                                    .compact();

            }
            //signature de la cle par rapport a la caine de caratere secretKey
            private Key getSignKey() {
                
                byte[] keyBytes = secretKey.getBytes();
                return new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
            }
            public Boolean validateToken(String token, UserDetails userDetails){
                String username = extraUsername(token);//va nous permettre d extraire du token l username
                return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));//verification si le username que j ai extrait du token est egale au username qu on va me fournir dans mes details uilisateur (userDetails) et on verifie aussi que le token n a pas expirer
                                                            }
                                                
            private boolean isTokenExpired(String token) {
                return extraExpirationDate(token).before(new Date());// lorsqque je recupere mon token je dois verifier que la date d expiration n est pas encore passe
            }
            private Date extraExpirationDate(String token){ // methode pour extraire la date d expiration
                return extractClaim(token,Claims::getExpiration);
            }
            public String extraUsername(String token) {
                return extractClaim(token, Claims::getSubject);//ca prend le token et l username que je veux extraire
            }
            private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
                final Claims claims = extraAllClaim(token);//pour recuperer tous les claim qu on doit avoir dans mon token
                                return claimsResolver.apply(claims);//pour recuperer le claim que je cherche
                            }
                
            private Claims extraAllClaim(String token) {//nous permet d avoir toutes les cles
                return Jwts.parser()
                           .setSigningKey(getSignKey())
                           .parseClaimsJws(token)
                           .getBody();
            }
}

