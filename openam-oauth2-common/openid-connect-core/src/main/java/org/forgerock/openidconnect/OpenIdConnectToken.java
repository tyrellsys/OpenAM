/*
 * The contents of this file are subject to the terms of the Common Development and
 * Distribution License (the License). You may not use this file except in compliance with the
 * License.
 *
 * You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the
 * specific language governing permission and limitations under the License.
 *
 * When distributing Covered Software, include this CDDL Header Notice in each file and include
 * the License file at legal/CDDLv1.0.txt. If applicable, add the following below the CDDL
 * Header, with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions copyright [year] [name of copyright owner]".
 *
 * Copyright 2014 ForgeRock AS.
 */

package org.forgerock.openidconnect;

import org.forgerock.oauth2.core.Token;
import org.forgerock.oauth2.core.exceptions.ServerException;
import org.forgerock.json.fluent.JsonValue;
import org.forgerock.json.jose.builders.JwtBuilderFactory;
import org.forgerock.json.jose.jws.JwsAlgorithm;
import org.forgerock.json.jose.jws.JwsHeader;
import org.forgerock.json.jose.jws.SignedJwt;
import org.forgerock.oauth2.core.OAuth2Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.PrivateKey;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.Map;

import static org.forgerock.oauth2.core.Utils.isEmpty;

/**
 * Models an OpenId Connect Token.
 *
 * @since 12.0.0
 */
public class OpenIdConnectToken extends JsonValue implements Token {

    private final Logger logger = LoggerFactory.getLogger("OAuth2Provider");
    private final JwtBuilderFactory jwtBuilderFactory = new JwtBuilderFactory();
    private final PrivateKey privateKey;
    private final String algorithm;

    /**
     * Constructs a new OpenIdConnectToken.
     *
     * @param privateKey The private key.
     * @param algorithm The algorithm.
     * @param iss The issuer.
     * @param sub The subject.
     * @param aud The audience.
     * @param azp The authorized party.
     * @param exp The expiry time.
     * @param iat The issued at time.
     * @param ath The authenticated time.
     * @param nonce The nonce.
     * @param ops The ops.
     */
    public OpenIdConnectToken(PrivateKey privateKey, String algorithm, String iss, String sub, String aud, String azp,
            long exp, long iat, long ath, String nonce, String ops) {
        super(new HashMap<String, Object>());
        this.privateKey = privateKey;
        this.algorithm = algorithm;
        setIss(iss);
        setSub(sub);
        setAud(aud);
        setAzp(azp);
        setExp(exp);
        setIat(iat);
        setAth(ath);
        setNonce(nonce);
        setOps(ops);
        setTokenType(OAuth2Constants.JWTTokenParams.JWT_TOKEN);
        setTokenName(OAuth2Constants.JWTTokenParams.ID_TOKEN);
    }

    /**
     * Sets a value on the OpenId Connect token if the value is not null or an empty String.
     *
     * @param key The key. Must not be {@code null}.
     * @param value The value.
     */
    private void set(String key, String value) {
        if (!isEmpty(value)) {
            put(key, value);
        }
    }

    /**
     * Sets the issuer.
     *
     * @param iss The issuer.
     */
    private void setIss(String iss) {
        set(OAuth2Constants.JWTTokenParams.ISS, iss);
    }

    /**
     * Sets the subject.
     *
     * @param sub The subject.
     */
    private void setSub(String sub) {
        set(OAuth2Constants.JWTTokenParams.SUB, sub);
    }

    /**
     * Sets the audience.
     *
     * @param aud The audience.
     */
    private void setAud(String aud) {
        set(OAuth2Constants.JWTTokenParams.AUD, aud);
    }

    /**
     * Sets the authorized party.
     *
     * @param azp The authorized party.
     */
    private void setAzp(String azp) {
        set(OAuth2Constants.JWTTokenParams.AZP, azp);
    }

    /**
     * Sets the expiry time in seconds.
     *
     * @param exp The expiry time.
     */
    private void setExp(long exp) {
        put(OAuth2Constants.JWTTokenParams.EXP, exp);
    }

    /**
     * Sets the issued at time in seconds.
     *
     * @param iat The issued at time.
     */
    private void setIat(long iat) {
        put(OAuth2Constants.JWTTokenParams.IAT, iat);
    }

    /**
     * Sets the authenticated time in seconds.
     *
     * @param ath The authenticated time.
     */
    private void setAth(long ath) {
        put(OAuth2Constants.JWTTokenParams.ATH, ath);
    }

    /**
     * Sets the nonce.
     *
     * @param nonce The nonce.
     */
    private void setNonce(String nonce) {
        set(OAuth2Constants.JWTTokenParams.NONCE, nonce);
    }

    /**
     * Sets the ops.
     *
     * @param ops The ops.
     */
    private void setOps(String ops) {
        set(OAuth2Constants.JWTTokenParams.OPS, ops);
    }

    /**
     * Sets the token's type.
     *
     * @param tokenType The token's type.
     */
    private void setTokenType(String tokenType) {
        set(OAuth2Constants.CoreTokenParams.TOKEN_TYPE, tokenType);
    }

    /**
     * Sets the token's name.
     *
     * @param tokenName The token's name.
     */
    private void setTokenName(String tokenName) {
        set(OAuth2Constants.CoreTokenParams.TOKEN_NAME, tokenName);
    }

    /**
     * {@inheritDoc}
     */
    public String getTokenId() throws ServerException {
        try {
            return sign().build();
        } catch (SignatureException e) {
            logger.error("Cant get JWT id", e);
            throw new ServerException("Cant get JWT id");
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getTokenName() {
        return get(OAuth2Constants.CoreTokenParams.TOKEN_NAME).asString();
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Object> toMap() throws ServerException {
        final Map<String, Object> tokenMap = new HashMap<String, Object>();
        try {
            tokenMap.put(OAuth2Constants.JWTTokenParams.ID_TOKEN, sign());
        } catch (SignatureException e) {
            logger.error("Cant sign JWT", e);
            throw new ServerException("Cant sign JWT");
        }
        return tokenMap;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Object> getTokenInfo() {
        return new HashMap<String, Object>();
    }

    /**
     * Signs the OpenId Connect token.
     *
     * @return A SignedJwt
     * @throws SignatureException If an error occurs with the signing of the OpenId Connect token.
     */
    public SignedJwt sign() throws SignatureException {
        final JwsAlgorithm jwsAlgorithm = JwsAlgorithm.valueOf(algorithm);
        if (jwsAlgorithm == null){
            logger.error("Unable to find jws algorithm for: " + algorithm);
            throw new SignatureException();
        }
        final JwsHeader header = new JwsHeader();
        header.setAlgorithm(jwsAlgorithm);
        header.setContentType("JWT");
        return new SignedJwt(header, jwtBuilderFactory.claims().claims(asMap()).build(), privateKey);
    }
}