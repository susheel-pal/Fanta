package com.example.fanta

class ModelPost {
    constructor() {}

    var description: String? = null
    var pid: String? = null
    var ptime: String? = null
    var pcomments: String? = null
    var title: String? = null
    var udp: String? = null
    var uemail: String? = null
    var uid: String? = null
    var uimage: String? = null
    var uname: String? = null
    var plike: String? = null

    constructor(
        description: String?,
        pid: String?,
        ptime: String?,
        pcomments: String?,
        title: String?,
        udp: String?,
        uemail: String?,
        uid: String?,
        uimage: String?,
        uname: String?,
        plike: String?
    ) {
        this.description = description
        this.pid = pid
        this.ptime = ptime
        this.pcomments = pcomments
        this.title = title
        this.udp = udp
        this.uemail = uemail
        this.uid = uid
        this.uimage = uimage
        this.uname = uname
        this.plike = plike
    }


    @JvmName("getPlike1")
    fun getPlike(): String? {
        return plike
    }

    @JvmName("setPlike1")
    fun setPlike(plike: String?){
        this.plike = plike
    }


    @JvmName("getPtime1")
    fun getPtime(): String? {
        return ptime
    }

    @JvmName("setPtime1")
    fun setPtime(ptime: String?){
        this.ptime = ptime
    }

    @JvmName("getTitle1")
    fun getTitle(): String? {
        return title
    }

    @JvmName("setTitle1")
    fun setTitle(title: String?){
        this.title = title
    }

    @JvmName("getDescription1")
    fun getDescription(): String? {
        return description
    }

    @JvmName("setDescription1")
    fun setDescription(description: String?){
        this.description = description
    }

    @JvmName("getPid1")
    fun getPid() :String? {
        return pid
    }

    @JvmName("setPid1")
    fun setPid(pid: String?){
        this.pid = pid
    }

    @JvmName("getPcomments1")
    fun getPcomments(): String? {
        return pcomments
    }

    @JvmName("setPcomments1")
    fun setPcomments(pcomments: String?) {
        this.pcomments = pcomments
    }

    @JvmName("getUdp1")
    fun getUdp(): String? {
        return udp
    }

    @JvmName("setUdp1")
    fun setUdp(udp: String?){
        this.udp = udp
    }

    @JvmName("getUemail1")
    fun getUemail(): String? {
        return uemail
    }

    @JvmName("setUemail1")
    fun setUemail(uemail: String?) {
        this.uemail = uemail
    }

    @JvmName("getUid1")
    fun getUid(): String? {
        return uid
    }

    @JvmName("setUid1")
    fun setUid(uid: String?) {
        this.uid = uid
    }

    @JvmName("getUimage1")
    fun getUimage(): String? {
        return uimage
    }

    @JvmName("setUimage1")
    fun setUimage(uimage: String?) {
        this.uimage = uimage
    }

    @JvmName("getUname1")
    fun getUname(): String? {
        return uname
    }

    @JvmName("setUname1")
    fun setUname(uname: String?) {
        this.uname = uname
    }

}