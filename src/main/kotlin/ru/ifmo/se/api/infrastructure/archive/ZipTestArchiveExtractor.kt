package ru.ifmo.se.api.infrastructure.archive

import org.springframework.stereotype.Component
import ru.ifmo.se.api.application.port.out.ExtractedArchiveTest
import ru.ifmo.se.api.application.port.out.TestArchiveExtractor
import ru.ifmo.se.api.domain.exception.TestBadRequestException
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

@Component
class ZipTestArchiveExtractor : TestArchiveExtractor {
    override fun extract(archiveBytes: ByteArray): List<ExtractedArchiveTest> {
        val inMap = mutableMapOf<String, String>()
        val outMap = mutableMapOf<String, String>()

        try {
            ZipInputStream(archiveBytes.inputStream()).use { zis ->
                var entry: ZipEntry? = zis.nextEntry
                while (entry != null) {
                    if (!entry.isDirectory) {
                        val name = entry.name.substringAfterLast('/')
                        when {
                            name.endsWith(".in") -> {
                                val baseName = name.removeSuffix(".in")
                                inMap[baseName] = zis.readBytes().toString(Charsets.UTF_8)
                            }

                            name.endsWith(".out") -> {
                                val baseName = name.removeSuffix(".out")
                                outMap[baseName] = zis.readBytes().toString(Charsets.UTF_8)
                            }

                            else -> {
                                throw TestBadRequestException("The archive is malformed or a file has no pair.")
                            }
                        }
                    }
                    zis.closeEntry()
                    entry = zis.nextEntry
                }
            }
        } catch (e: Exception) {
            if (e is TestBadRequestException) throw e
            throw TestBadRequestException("The archive is malformed or a file has no pair.")
        }

        if (inMap.isEmpty() || inMap.keys != outMap.keys) {
            throw TestBadRequestException("The archive is malformed or a file has no pair.")
        }

        return inMap.keys.sorted().map { baseName ->
            ExtractedArchiveTest(
                input = inMap[baseName]!!,
                output = outMap[baseName]!!,
            )
        }
    }
}
