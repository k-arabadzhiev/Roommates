package org.diploma.security.hashing

import io.ktor.util.*
import java.security.SecureRandom

class SHA256HashingService : HashingService {
    override fun generateSaltedHash(value: String, saltLength: Int): SaltedHash {
        val salt = SecureRandom.getInstance("SHA1PRNG").generateSeed(saltLength)
        val saltAsHex = hex(salt)
        val hashBytes = getDigestFunction("SHA-256") { saltAsHex }
        val hash = hex(hashBytes(value))
        return SaltedHash(
            hash = hash,
            salt = saltAsHex
        )
    }

    override fun verify(value: String, saltedHash: SaltedHash): Boolean {
        val hashBytes = getDigestFunction("SHA-256") { saltedHash.salt }
        val hash = hex(hashBytes(value))
        return hash == saltedHash.hash
    }
}