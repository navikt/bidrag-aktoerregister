package no.nav.bidrag.aktoerregister.dto.enumer

enum class Diskresjonskode {
    SPFO,
    SPSF,
    P19,
    ;

    companion object {
        fun valueOf(type: String?): Diskresjonskode? = Diskresjonskode.values().find { it.name == type }
    }
}
