package io.dotlottie.loader

import com.squareup.moshi.JsonReader
import com.squareup.moshi.Moshi
import io.dotlottie.loader.models.DotLottie
import io.dotlottie.loader.models.DotLottieConverter
import io.dotlottie.loader.models.Manifest
import io.dotlottie.loader.models.manifestAdapter
import okio.`-DeprecatedOkio`.buffer
import okio.`-DeprecatedOkio`.source
import okio.buffer
import okio.source
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream


class DefaultDotLottieConverter: DotLottieConverter {

    private fun String.lastSegmentName() = split("/").last()
    private fun String.withoutExt() = split(".").first()



    override fun parseZipInputStream(inputStream: ZipInputStream): DotLottie? {

        var manifest: Manifest? = null
        var animations: HashMap<String, ByteArray> = HashMap()

        inputStream.use {
            var entry: ZipEntry? = it.nextEntry
            while (entry!=null) {

                // parse the manifest
                val name = entry.name
                if(name.equals("manifest.json", ignoreCase = true)) {
                    manifest = manifestAdapter.fromJson(JsonReader.of(it.source().buffer()))
                } else if(name.startsWith("animations/")) {
                    animations[name.lastSegmentName().withoutExt()] = it.readBytes()
                }

                it.closeEntry()
                entry = it.nextEntry
            }
        }

        return DotLottie(
            manifest,
            animations
        )

    }

    override fun parseFileInputStream(inputStream: InputStream): DotLottie? {
        TODO("Not yet implemented")
    }

}