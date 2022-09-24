import io.circe.{Codec, Decoder, Encoder}
import io.circe.generic.semiauto.deriveCodec

import java.nio.file.{Path, Paths}

case class SiteManifest(distDir: Path, local: Boolean)

object SiteManifest {
  implicit val path: Codec[Path] = Codec.from(
    Decoder.decodeString.map(s => Paths.get(s)),
    Encoder.encodeString.contramap(_.toAbsolutePath.toString)
  )
  implicit val json: Codec[SiteManifest] = deriveCodec[SiteManifest]
}
