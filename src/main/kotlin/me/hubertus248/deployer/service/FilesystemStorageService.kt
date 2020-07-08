package me.hubertus248.deployer.service

import me.hubertus248.deployer.data.entity.FileKey
import me.hubertus248.deployer.data.entity.FilesystemFileMetadata
import me.hubertus248.deployer.reposiotry.FilesystemFileMetadataRepository
import me.hubertus248.deployer.util.Util
import org.apache.commons.io.IOUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import java.nio.file.Path
import java.nio.file.Paths
import javax.annotation.PostConstruct
import javax.transaction.Transactional

interface FilesystemStorageService {
    fun saveFile(inputStream: InputStream, filename: String, contentType: String): FilesystemFileMetadata

    fun getFileContent(fileKey: FileKey): InputStream?

    fun getFile(fileKey: FileKey): Pair<InputStream, FilesystemFileMetadata>?

    fun deleteFile(fileKey: FileKey)

    fun checkFileExists(fileKey: FileKey): Boolean

    fun getFileMetadata(fileKey: FileKey): FilesystemFileMetadata?
}

@Service
class FilesystemStorageServiceImpl(
        private val filesystemFileMetadataRepository: FilesystemFileMetadataRepository
) : FilesystemStorageService {

    private val util: Util = Util()

    @Value("\${deployer.filesystem-storage-path}")
    private lateinit var dataRoot: String
    private lateinit var dataStorePath: String

    @PostConstruct
    fun initializeDataStore() {
        createDataStore()
    }

    private fun createDataStore() {
        dataStorePath = Path.of(dataRoot, "staticFiles").toString()
        val dataStoreRootDir = File(dataStorePath)
        if (dataStoreRootDir.exists()) {
            if (dataStoreRootDir.isDirectory) return
            else throw IllegalStateException("DataStore \"$dataStorePath\" already exists and is not a directory")
        } else {
            dataStoreRootDir.mkdirs()
        }
    }

    @Transactional
    override fun saveFile(inputStream: InputStream,
                          filename: String,
                          contentType: String
    ): FilesystemFileMetadata {
        val key = FileKey(util.secureReadableRandomString(10))

        val file = getFileFromKey(key.value)

        if (file.exists()) {//TODO should generate illegalStateException instead
            if (file.isDirectory || !file.isFile)
                throwExceptionDataStoreCorrupted(key.value)
            file.delete()
            filesystemFileMetadataRepository.deleteAllByFileKey(key)
        }

        val metadata = FilesystemFileMetadata(0, key, filename, contentType)
        filesystemFileMetadataRepository.save(metadata)

        file.parentFile.mkdirs()//TODO check if failed

        val outputStream = FileOutputStream(file)
        IOUtils.copyLarge(inputStream, outputStream)
        IOUtils.closeQuietly(outputStream)
        return metadata
    }

    override fun getFileContent(fileKey: FileKey): InputStream? {
        val file = getFileFromKey(fileKey.value)

        if (!file.exists()) return null
        if (file.isDirectory || !file.isFile)
            throwExceptionDataStoreCorrupted(fileKey.value)

        return FileInputStream(file)
    }

    override fun getFile(fileKey: FileKey): Pair<InputStream, FilesystemFileMetadata>? {
        val file = getFileFromKey(fileKey.value)
        val metadata = filesystemFileMetadataRepository.findFirstByFileKey(fileKey) ?: return null

        if (!file.exists()) return null
        if (file.isDirectory || !file.isFile)
            throwExceptionDataStoreCorrupted(fileKey.value)

        return Pair<InputStream, FilesystemFileMetadata>(FileInputStream(file), metadata)
    }

    @Transactional
    override fun deleteFile(fileKey: FileKey) {
        val metadata = filesystemFileMetadataRepository.findFirstByFileKey(fileKey) ?: return

        val file = getFileFromKey(fileKey.value)
        if (!file.exists()) return

        if (file.isDirectory || !file.isFile)
            throwExceptionDataStoreCorrupted(fileKey.value)

        file.delete()//TODO check if failed
        deleteDirIfEmpty(file.parentFile)
        deleteDirIfEmpty(file.parentFile.parentFile)

        filesystemFileMetadataRepository.deleteAllByFileKey(metadata.fileKey)
    }

    override fun checkFileExists(fileKey: FileKey): Boolean {
        TODO("Not yet implemented")
    }

    override fun getFileMetadata(fileKey: FileKey): FilesystemFileMetadata? {
        TODO("Not yet implemented")
    }

    private fun getFileFromKey(key: String): File = Paths.get(dataStorePath,
            key.substring(0, 2),
            key.substring(2, 4),
            key).toFile()

    private fun throwExceptionDataStoreCorrupted(key: String): Nothing =
            throw IllegalStateException("Requested file $key is not a regular file - datastore corrupted!")

    private fun deleteDirIfEmpty(file: File) {
        if (file.listFiles()?.isEmpty() ?: throw IllegalArgumentException("Not a directory!")) {
            file.delete()
        }
    }
}