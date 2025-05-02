package nl.andarabski.hogwartsartifactsonline.artifact;

import jakarta.persistence.*;
import nl.andarabski.hogwartsartifactsonline.wizard.Wizard;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serializable;

@Entity
public class Artifact implements Serializable {

    @Id
//    @GeneratedValue(generator = "uuid")
//    @GenericGenerator(name = "uuid", strategy = "uuid2")
//    @Column(columnDefinition = "VARCHAR(36)")
    private String id;
    private String name;
    private String description;
    private String imageUrl;

    @ManyToOne
    private Wizard owner;


    public Artifact() {}

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Wizard getOwner() {
        return owner;
    }
    public void setOwner(Wizard owner) {
        this.owner = owner;
    }
}
