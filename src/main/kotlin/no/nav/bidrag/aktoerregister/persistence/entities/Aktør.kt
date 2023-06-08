package no.nav.bidrag.aktoerregister.persistence.entities

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Version
import java.sql.Timestamp
import java.time.LocalDate

@Entity(name = "aktoer")
data class Aktør(

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @Column(name = "aktoer_ident")
    var aktørIdent: String,

    @Column(name = "aktoertype")
    var aktørType: String,

    @Column(name = "offentlig_id")
    var offentligId: String? = null,

    @Column(name = "offentlig_id_type")
    var offentligIdType: String? = null,

    @Column(name = "norskkontonr")
    var norskKontonr: String? = null,

    @Column(name = "iban")
    var iban: String? = null,

    @Column(name = "swift")
    var swift: String? = null,

    @Column(name = "banknavn")
    var bankNavn: String? = null,

    @Column(name = "banklandkode")
    var bankLandkode: String? = null,

    @Column(name = "bankcode")
    var bankCode: String? = null,

    @Column(name = "valutakode")
    var valutaKode: String? = null,

    @Column(name = "fornavn")
    var fornavn: String? = null,

    @Column(name = "etternavn")
    var etternavn: String? = null,

    @Column(name = "fodt_dato")
    var fødtDato: LocalDate? = null,

    @Column(name = "dod_dato")
    var dødDato: LocalDate? = null,

    @Column(name = "gradering")
    var gradering: String? = null,

    @Column(name = "sprakkode")
    var språkkode: String? = null,

    @Column(name = "adresselinje1")
    var adresselinje1: String? = null,

    @Column(name = "adresselinje2")
    var adresselinje2: String? = null,

    @Column(name = "adresselinje3")
    var adresselinje3: String? = null,

    @Column(name = "leilighetsnummer")
    var leilighetsnummer: String? = null,

    @Column(name = "postnr")
    var postnr: String? = null,

    @Column(name = "poststed")
    var poststed: String? = null,

    @Column(name = "land")
    var land: String? = null,

    @OneToMany(mappedBy = "aktør", cascade = [CascadeType.ALL], orphanRemoval = true)
    var tidligereIdenter: MutableSet<TidligereIdenter> = mutableSetOf(),

    @OneToOne(mappedBy = "aktør", cascade = [CascadeType.ALL], orphanRemoval = true)
    var dødsbo: Dødsbo? = null,

    @OneToMany(mappedBy = "aktør", cascade = [CascadeType.ALL], orphanRemoval = true)
    val hendelser: MutableList<Hendelse> = ArrayList(),

    @Version
    @Column(name = "sist_endret")
    val sistEndret: Timestamp? = null

) {

    fun addHendelse(hendelse: Hendelse) {
        hendelser.add(hendelse)
    }

    fun oppdaterAlleFelter(aktør: Aktør) {
        this.aktørIdent = aktør.aktørIdent
        this.aktørType = aktør.aktørType
        this.offentligId = aktør.offentligId
        this.offentligIdType = aktør.offentligIdType
        this.norskKontonr = aktør.norskKontonr
        this.iban = aktør.iban
        this.swift = aktør.swift
        this.bankNavn = aktør.bankNavn
        this.bankLandkode = aktør.bankLandkode
        this.bankCode = aktør.bankCode
        this.valutaKode = aktør.valutaKode
        this.fornavn = aktør.fornavn
        this.etternavn = aktør.etternavn
        this.fødtDato = aktør.fødtDato
        this.dødDato = aktør.dødDato
        this.gradering = aktør.gradering
        this.språkkode = aktør.språkkode
        this.adresselinje1 = aktør.adresselinje1
        this.adresselinje2 = aktør.adresselinje2
        this.adresselinje3 = aktør.adresselinje3
        this.leilighetsnummer = aktør.leilighetsnummer
        this.valutaKode = aktør.valutaKode
        this.poststed = aktør.poststed
        this.postnr = aktør.postnr
        this.land = aktør.land
        this.dødsbo = aktør.dødsbo
        this.tidligereIdenter.forEach { it.aktør = null }
        this.tidligereIdenter = aktør.tidligereIdenter
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Aktør

        if (aktørIdent != other.aktørIdent) return false
        if (aktørType != other.aktørType) return false
        if (offentligId != other.offentligId) return false
        if (offentligIdType != other.offentligIdType) return false
        if (norskKontonr != other.norskKontonr) return false
        if (iban != other.iban) return false
        if (swift != other.swift) return false
        if (bankNavn != other.bankNavn) return false
        if (bankLandkode != other.bankLandkode) return false
        if (bankCode != other.bankCode) return false
        if (valutaKode != other.valutaKode) return false
        if (fornavn != other.fornavn) return false
        if (etternavn != other.etternavn) return false
        if (fødtDato != other.fødtDato) return false
        if (dødDato != other.dødDato) return false
        if (gradering != other.gradering) return false
        if (språkkode != other.språkkode) return false
        if (adresselinje1 != other.adresselinje1) return false
        if (adresselinje2 != other.adresselinje2) return false
        if (adresselinje3 != other.adresselinje3) return false
        if (leilighetsnummer != other.leilighetsnummer) return false
        if (postnr != other.postnr) return false
        if (poststed != other.poststed) return false
        if (land != other.land) return false
        if (dødsbo != other.dødsbo) return false
        if (!erTidligereIdenterLike(other)) return false

        return true
    }

    fun erTidligereIdenterLike(other: Aktør): Boolean {
        return tidligereIdenter.size == other.tidligereIdenter.size &&
            tidligereIdenter.all { a1 ->
                other.tidligereIdenter.any { a2 -> a1.tidligereAktoerIdent == a2.tidligereAktoerIdent } &&
                    other.tidligereIdenter.all { a2 ->
                        tidligereIdenter.any { a1 -> a2.tidligereAktoerIdent == a1.tidligereAktoerIdent }
                    }
            }
    }

    override fun hashCode(): Int {
        var result = aktørIdent.hashCode()
        result = 31 * result + aktørType.hashCode()
        result = 31 * result + (offentligId?.hashCode() ?: 0)
        result = 31 * result + (offentligIdType?.hashCode() ?: 0)
        result = 31 * result + (norskKontonr?.hashCode() ?: 0)
        result = 31 * result + (iban?.hashCode() ?: 0)
        result = 31 * result + (swift?.hashCode() ?: 0)
        result = 31 * result + (bankNavn?.hashCode() ?: 0)
        result = 31 * result + (bankLandkode?.hashCode() ?: 0)
        result = 31 * result + (bankCode?.hashCode() ?: 0)
        result = 31 * result + (valutaKode?.hashCode() ?: 0)
        result = 31 * result + (fornavn?.hashCode() ?: 0)
        result = 31 * result + (etternavn?.hashCode() ?: 0)
        result = 31 * result + (fødtDato?.hashCode() ?: 0)
        result = 31 * result + (dødDato?.hashCode() ?: 0)
        result = 31 * result + (gradering?.hashCode() ?: 0)
        result = 31 * result + (språkkode?.hashCode() ?: 0)
        result = 31 * result + (adresselinje1?.hashCode() ?: 0)
        result = 31 * result + (adresselinje2?.hashCode() ?: 0)
        result = 31 * result + (adresselinje3?.hashCode() ?: 0)
        result = 31 * result + (leilighetsnummer?.hashCode() ?: 0)
        result = 31 * result + (postnr?.hashCode() ?: 0)
        result = 31 * result + (poststed?.hashCode() ?: 0)
        result = 31 * result + (land?.hashCode() ?: 0)
        result = 31 * result + dødsbo?.id.hashCode()
        return result
    }

    override fun toString(): String {
        return "Aktoer(id=$id, " +
            "aktoerIdent='$aktørIdent', " +
            "aktoerType='$aktørType', " +
            "offentligId=$offentligId, " +
            "offentligIdType=$offentligIdType, " +
            "norskKontonr=$norskKontonr, " +
            "iban=$iban, " +
            "swift=$swift, " +
            "bankNavn=$bankNavn, " +
            "bankLandkode=$bankLandkode, " +
            "bankCode=$bankCode, " +
            "valutaKode=$valutaKode, " +
            "fornavn=$fornavn, " +
            "etternavn=$etternavn, " +
            "fodtDato=$fødtDato, " +
            "dodDato=$dødDato, " +
            "gradering=$gradering, " +
            "sprakkode=$språkkode, " +
            "adresselinje1=$adresselinje1, " +
            "adresselinje2=$adresselinje2, " +
            "adresselinje3=$adresselinje3, " +
            "leilighetsnummer=$leilighetsnummer, " +
            "postnr=$postnr, " +
            "poststed=$poststed, land=$land, " +
            "tidligereIdenter=$tidligereIdenter, " +
            "dodsbo=${dødsbo?.id}, " +
            "sistEndret=$sistEndret)"
    }
}
