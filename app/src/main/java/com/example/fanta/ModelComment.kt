package com.example.fanta

class ModelComment {
    var cId: String? = null
    var comment: String? = null
    var ptime: String? = null
    var udp: String? = null
    var uname: String? = null
    var uemail: String? = null
    var uid: String? = null

    constructor() {}

    constructor(cId: String?, comment: String?, ptime: String?, udp: String?, uemail: String?, uid: String?, uname: String?) {
        this.cId = cId
        this.comment = comment
        this.ptime = ptime
        this.udp = udp
        this.uemail = uemail
        this.uid = uid
        this.uname = uname
    }

    @JvmName("getCid1")
    fun getCid(): String? {
        return cId
    }

    @JvmName("setCid1")
    fun setCid(cId: String?) {
        this.cId = cId
    }

    @JvmName("getComment1")
    fun getComment(): String? {
        return comment
    }

    @JvmName("setComment1")
    fun setComment(comment: String?) {
        this.comment = comment
    }

    @JvmName("getPtime1")
    fun getPtime(): String? {
        return ptime
    }

    @JvmName("setPtime1")
    fun setPtime(ptime: String?) {
        this.ptime = ptime
    }

    @JvmName("getUdp1")
    fun getUdp(): String? {
        return udp
    }

    @JvmName("setUdp1")
    fun setUdp(udp: String?) {
        this.udp = udp
    }

    @JvmName("getUname1")
    fun getUname(): String? {
        return uname
    }

    @JvmName("setUname1")
    fun setUname(uname: String?) {
        this.uname = uname
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

}