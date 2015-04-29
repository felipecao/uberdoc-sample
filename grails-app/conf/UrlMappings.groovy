class UrlMappings {

	static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/"(view:"/index")
        "500"(view:'/error')

        "/api/phods"    (controller: 'phod', action: [POST: "create"], parseRequest: true)
        "/api/phods/$id"(controller: 'phod', action: [PUT: "update", PATCH: "update", GET: "get", DELETE: "delete"], parseRequest: true)
	}
}
