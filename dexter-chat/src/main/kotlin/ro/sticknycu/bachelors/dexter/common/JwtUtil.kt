package ro.sticknycu.bachelors.dexter.common

import org.springframework.security.oauth2.jwt.Jwt

object JwtUtil {
    @JvmStatic
    fun extractUserName(jwt: Jwt): String {
        return jwt.getClaimAsString("preferred_username")
    }
}
