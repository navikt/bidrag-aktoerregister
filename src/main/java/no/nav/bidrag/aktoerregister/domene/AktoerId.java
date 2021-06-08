package no.nav.bidrag.aktoerregister.domene;

public class AktoerId {
    private String aktoerId;
    private Identtype identtype;

    public AktoerId() {
    }

    public AktoerId(String kundeId, Identtype identtype) {
        this.aktoerId = kundeId;
        this.identtype = identtype;
    }

    public String getAktoerId() {
        return aktoerId;
    }

    public void setAktoerId(String aktoer) {
        this.aktoerId = aktoer;
    }

    public Identtype getIdenttype() {
        return identtype;
    }

    public void setIdenttype(Identtype identtype) {
        this.identtype = identtype;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((identtype == null) ? 0 : identtype.hashCode());
        result = prime * result + ((aktoerId == null) ? 0 : aktoerId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AktoerId other = (AktoerId) obj;
        if (identtype != other.identtype)
            return false;
        if (aktoerId == null) {
            if (other.aktoerId != null)
                return false;
        } else if (!aktoerId.equals(other.aktoerId))
            return false;
        return true;
    }
}
