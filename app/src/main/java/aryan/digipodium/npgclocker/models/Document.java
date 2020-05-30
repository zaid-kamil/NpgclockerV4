package aryan.digipodium.npgclocker.models;

import java.io.Serializable;

public class Document implements Serializable {

   public String filename;
   public String fileurl;
   public String filetype;
   public String uploaderid;

    public Document() {
    }

    public Document(String filename, String fileurl, String filetype, String uploaderid) {
        this.filename = filename;
        this.fileurl = fileurl;
        this.filetype = filetype;
        this.uploaderid = uploaderid;
    }
}
