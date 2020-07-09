package me.hubertus248.deployer.data.entity

import javax.persistence.*

@Entity
class FilesystemFileMetadata(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long,

        @Embedded
        val fileKey: FileKey,

        @Column(nullable = false, unique = false, length = 255)
        val filename: String,

        @Column(nullable = false, unique = false, length = 255)
        val contentType: String,

        @Column(nullable = false, updatable = true)
        var deleted: Boolean = false
)

@Embeddable
data class FileKey(
        @Access(AccessType.FIELD)
        @Column(unique = true, nullable = false, updatable = false, length = 255, name = "fileKey")
        val value: String) {
    init {
        require(value.isNotEmpty())
        require(value.all { it.isLetterOrDigit() })
        require(value.length in 4..255)
        //TODO test
    }
}