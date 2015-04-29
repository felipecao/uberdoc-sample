package uberdoc.sample

import com.uberall.uberdoc.annotation.Error
import com.uberall.uberdoc.annotation.Errors
import com.uberall.uberdoc.annotation.HeaderParam
import com.uberall.uberdoc.annotation.HeaderParams
import com.uberall.uberdoc.annotation.QueryParam
import com.uberall.uberdoc.annotation.Resource
import sample.Pod

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Errors([
        @Error(errorCode = "XYZ123", httpCode = 412, description = "this is a general 412 error, to be applied to all actions/resources in this file"),
        @Error(errorCode = "ABC456", httpCode = 400, description = "this is a general 409 error, to be applied to all actions/resources in this file"),
        @Error(errorCode = "CONF409", httpCode = 409, description = "all actions in this file may throw a 409 to indicate conflict")
])
@HeaderParams([
        @HeaderParam(name = "publicToken", sampleValue = "all methods in this file should send a header param", description = "this param should be sent within the headers"),
        @HeaderParam(name = "other token", sampleValue = "just some other token that every method should send", description = "this param should also be sent within the headers")
])
@Transactional(readOnly = true)
class PhodController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    @Resource(responseObject = Pod, description = "this resource allows all Pods to be retrieved from DB")
    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Phod.list(params), model:[phodInstanceCount: Phod.count()]
    }

    @Resource(requestObject = Pod, responseCollectionOf = Pod, description = "this resource allows all Pods to be retrieved from DB")
    @Error(errorCode = "NF404", httpCode = 404, description = "returned if the resource does not exist")
    @QueryParam(name = "id", description = "the id of the Pod to be retrieved from DB", sampleValue = "4")
    def get(Phod phodInstance) {
        respond phodInstance
    }

    @Resource(object = Pod, description = "this resource creates Pods")
    @HeaderParam(name = "some header param", sampleValue = "hdr", description = "this is just something else to be sent on creation")
    @Transactional
    def create(Phod phodInstance) {
        if (phodInstance == null) {
            notFound()
            return
        }

        if (phodInstance.hasErrors()) {
            respond phodInstance.errors, view:'create'
            return
        }

        phodInstance.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'phod.label', default: 'Phod'), phodInstance.id])
                redirect phodInstance
            }
            '*' { respond phodInstance, [status: CREATED] }
        }
    }

    @Resource(object = Pod)
    @QueryParam(name = "id", description = "the id of the Pod to be retrieved from DB", sampleValue = "4")
    @Transactional
    def update(Phod phodInstance) {
        if (phodInstance == null) {
            notFound()
            return
        }

        if (phodInstance.hasErrors()) {
            respond phodInstance.errors, view:'edit'
            return
        }

        phodInstance.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'Phod.label', default: 'Phod'), phodInstance.id])
                redirect phodInstance
            }
            '*'{ respond phodInstance, [status: OK] }
        }
    }

    @QueryParam(name = "id", description = "the id of the Pod to be retrieved from DB", sampleValue = "4")
    @Transactional
    def delete(Phod phodInstance) {

        if (phodInstance == null) {
            notFound()
            return
        }

        phodInstance.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'Phod.label', default: 'Phod'), phodInstance.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'phod.label', default: 'Phod'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
