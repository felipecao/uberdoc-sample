package uberdoc.sample

import com.uberall.uberdoc.annotation.UberDocModel
import com.uberall.uberdoc.annotation.UberDocProperty

@UberDocModel(description = "this is a pessoa")
class Pessoa {

    @UberDocProperty(description = "the first name", sampleValue = "Manfred")
    String firstName
    @UberDocProperty(description = "the last name", sampleValue = "Schnösebrink")
    String lastName
    @UberDocProperty(description = "a list of nick names", sampleValue = "[manne, fredbert, heribert]")
    List<String> nickNames

    static transients = ['fullName']
    static hasMany = [nickNames: String, pods: Phod]
    static hasOne = [ship: Nave]

    String getFullName() {
        return firstName + " " + lastName
    }
}
