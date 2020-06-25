package com.dating.padosi

class User {
    var name: String? = null
    var password: String? = null
    var email: String? = null
    var image: String? = null
    var number: String? = null
    var uid: String? = null
    var about: String? = null
    var gender: String? = null
    var city: String? = null
    var state : String?=null
    var country: String?=null
    var fcm:String?=null
    var dob:String?=null

    constructor(name: String?, password: String, email: String?, image: String?, number: String?, uid: String?,fcm:String)
    {
        this.name = name
        this.password = password
        this.email = email
        this.image = image
        this.number = number
        this.uid = uid
        this.fcm=fcm
    }



    constructor(name: String?, image: String?) {
        this.name = name
        this.image = image
    }

    constructor() {}
    constructor(
        name: String?,
        password: String?,
        email: String?,
        image: String?,
        number: String?,
        uid: String?,
        about: String?,
        gender: String?,
        city: String?,
        state: String?,
        country: String?
    ) {
        this.name = name
        this.password = password
        this.email = email
        this.image = image
        this.number = number
        this.uid = uid
        this.about = about
        this.gender = gender
        this.city = city
        this.state = state
        this.country = country
    }

    constructor(
        name: String?,
        password: String?,
        email: String?,
        image: String?,
        number: String?,
        uid: String?,
        about: String?,
        gender: String?,
        city: String?,
        state: String?,
        country: String?,
        fcm: String?,
        dob:String?
    ) {
        this.name = name
        this.password = password
        this.email = email
        this.image = image
        this.number = number
        this.uid = uid
        this.about = about
        this.gender = gender
        this.city = city
        this.state = state
        this.country = country
        this.fcm = fcm
        this.dob=dob
    }
}
