package javabio.storage.entities;

public class RefKey {

    private String refactorID;
    private String src;
    private String tgt;
    private String mth;
    private String fld;

    public RefKey(String refactorID, String src, String tgt, String mth, String fld) {
        super();
        this.refactorID = refactorID;
        this.src = src;
        this.tgt = tgt;
        this.mth = mth;
        this.fld = fld;
    }

    public String getRefactorID() {
        return refactorID;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getTgt() {
        return tgt;
    }

    public void setTgt(String tgt) {
        this.tgt = tgt;
    }

    public String getMth() {
        return mth;
    }

    public String getFld() {
        return fld;
    }

    public void setFld(String fld) {
        this.fld = fld;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((fld == null) ? 0 : fld.hashCode())
                + ((mth == null) ? 0 : mth.hashCode())
                + ((tgt == null) ? 0 : tgt.hashCode())
                + ((src == null) ? 0 : src.hashCode())
                + ((refactorID == null) ? 0 : refactorID.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final RefKey other = (RefKey) obj;

        //check refactor
        if (refactorID == null) {
            if (other.refactorID != null)
                return false;
        } else if (!refactorID.equals(other.refactorID))
            return false;

        //check src
        if (src == null) {
            if (other.src != null)
                return false;
        } else if (!src.equals(other.src))
            return false;

        //check tgt
        if (tgt == null) {
            if (other.tgt != null)
                return false;
        } else if (!tgt.equals(other.tgt))
            return false;

        //check mth
        if (mth == null) {
            if (other.mth != null)
                return false;
        } else if (!mth.equals(other.mth))
            return false;

        //check fld
        if (fld == null) {
            if (other.fld != null)
                return false;
        } else if (!fld.equals(other.fld))
            return false;

        return true;
    }


}
