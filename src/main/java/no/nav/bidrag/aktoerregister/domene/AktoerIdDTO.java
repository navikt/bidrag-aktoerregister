package no.nav.bidrag.aktoerregister.domene;

public class AktoerIdDTO {
    private String aktoerId;
    private IdenttypeDTO identtype;

    public AktoerIdDTO() {
    }

    public AktoerIdDTO(String aktoerId, IdenttypeDTO identtype) {
        this.aktoerId = aktoerId;
        this.identtype = identtype;
    }

    public String getAktoerId() {
        return aktoerId;
    }

    public void setAktoerId(String aktoer) {
        this.aktoerId = aktoer;
    }

    public IdenttypeDTO getIdenttype() {
        return identtype;
    }

    public void setIdenttype(IdenttypeDTO identtype) {
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
        AktoerIdDTO other = (AktoerIdDTO) obj;
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
