package com.github.manosbatsis.services.user.model

import com.github.manosbatsis.services.user.kafka.UserEntityListener
import jakarta.persistence.*
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.apache.commons.lang3.builder.ToStringBuilder
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Where
import java.time.LocalDateTime

@Entity
@Table(name = "users")
@SQLDelete(sql = "update users set deleted=true where id=?")
@Where(clause = "deleted = false")
@EntityListeners(
    UserEntityListener::class,
)
class User(
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @field:Column(nullable = false, unique = true)
    var email: String,
    @field:Column(nullable = false)
    var fullName: String,
    @field:Column(nullable = false)
    var address: String,
    @field:Column(nullable = false)
    var active: Boolean,
) {

    @Column(nullable = false)
    var deleted = java.lang.Boolean.FALSE

    @Column(nullable = false)
    var createdAt: LocalDateTime? = null

    @Column(nullable = false)
    var updatedAt: LocalDateTime? = null

    @PrePersist
    fun onPrePersist() {
        updatedAt = LocalDateTime.now()
        createdAt = updatedAt
    }

    @PreUpdate
    fun onPreUpdate() {
        updatedAt = LocalDateTime.now()
    }

    override fun equals(other: Any?): Boolean = when {
        other == null -> false
        other === this -> true
        other !is User -> false
        else -> {
            EqualsBuilder()
                .appendSuper(super.equals(other))
                .append(id, other.id)
                .append(email, other.email)
                .append(fullName, other.fullName)
                .append(address, other.address)
                .append(active, other.active)
                .isEquals
        }
    }

    override fun hashCode(): Int =
        HashCodeBuilder(21, 33)
            .append(id)
            .append(email)
            .append(fullName)
            .append(address)
            .append(active)
            .toHashCode()

    override fun toString(): String = ToStringBuilder(this)
        .append("id", id)
        .append("email", email)
        .append("fullName", fullName)
        .append("active", active)
        .append("deleted", deleted)
        .toString()
}
