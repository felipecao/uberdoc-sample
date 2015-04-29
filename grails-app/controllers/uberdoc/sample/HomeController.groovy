package uberdoc.sample

class HomeController {

    def uberDocService

    def index() {
        def m = uberDocService.apiDocs
        println "hey!"
    }
}
