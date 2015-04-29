package uberdoc.sample

class Pessoa {

    String firstName
    String lastName
    List<String> nickNames

    static transients = ['fullName']
    static hasMany = [nickNames: String, pods: Phod]
    static hasOne = [ship: Nave]

    String getFullName() {
        return firstName + " " + lastName
    }
}
