package uberdoc.sample

import com.uberall.uberdoc.annotation.UberDocError
import com.uberall.uberdoc.annotation.UberDocErrors
import com.uberall.uberdoc.annotation.UberDocHeader
import com.uberall.uberdoc.annotation.UberDocHeaders
import com.uberall.uberdoc.annotation.UberDocQueryParam
import com.uberall.uberdoc.annotation.UberDocQueryParams
import com.uberall.uberdoc.annotation.UberDocResource
import com.uberall.uberdoc.annotation.UberDocUriParam
import com.uberall.uberdoc.annotation.UberDocUriParams
import sample.Pod

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@UberDocErrors([
        @UberDocError(errorCode = "XYZ123", httpCode = 412, description = "this is a general 412 error, to be applied to all actions/resources in this file"),
        @UberDocError(errorCode = "ABC456", httpCode = 400, description = "this is a general 409 error, to be applied to all actions/resources in this file"),
        @UberDocError(errorCode = "CONF409", httpCode = 409, description = "all actions in this file may throw a 409 to indicate conflict")
])
@UberDocHeaders([
        @UberDocHeader(name = "publicToken", sampleValue = "all methods in this file should send a header param", description = "this param should be sent within the headers"),
        @UberDocHeader(name = "other token", sampleValue = "just some other token that every method should send", description = "this param should also be sent within the headers")
])
@Transactional(readOnly = true)
class PhodController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    @UberDocResource(responseObject = Pod, description = "this resource allows all Pods to be retrieved from DB")
    @UberDocQueryParams([
            @UberDocQueryParam(name = "offset", required = false, description = "offset used for pagination"),
            @UberDocQueryParam(name = "locationIds", required = false, description = "collection of ids to be retrieved", isCollection = true)
    ])
    @UberDocQueryParam(name = "max", required = true, description = "Maximum number of records to display")
    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Phod.list(params), model:[phodInstanceCount: Phod.count()]
    }

    @UberDocResource(requestObject = Pod, responseCollectionOf = Pod, description = "this resource allows all Pods to be retrieved from DB")
    @UberDocError(errorCode = "NF404", httpCode = 404, description = "returned if the resource does not exist")
    @UberDocUriParam(name = "other id", description = "the other id of the Pod to be retrieved from DB", sampleValue = "4")
    def get(Phod phodInstance) {
        respond phodInstance
    }

    @UberDocResource(object = Pod, description = "this resource creates Pods")
    @UberDocHeader(name = "some header param", sampleValue = "hdr", description = "this is just something else to be sent on creation")
    @UberDocHeaders([
            @UberDocHeader(name = "a token", sampleValue = "just a sample token", description = "param used just for testing purposes")
    ])
    @UberDocErrors([
            @UberDocError(errorCode = "XYZ666", httpCode = 666, description = "666 - the beast has attacked this controller")
    ])
    @UberDocError(errorCode = "XYZ999", httpCode = 999, description = "999 - the reverse beast has attacked this controller")
    @UberDocUriParams([
            @UberDocUriParam(name = "firstId", description = "the first id", sampleValue = "4"),
            @UberDocUriParam(name = "secondId", description = "the second id", sampleValue = "4")
    ])
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

    @UberDocResource(object = Pod)
    @UberDocQueryParam(name = "id", description = "the id of the Pod to be retrieved from DB", sampleValue = "4")
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

    @UberDocQueryParam(name = "id", description = "the id of the Pod to be retrieved from DB", sampleValue = "4")
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
