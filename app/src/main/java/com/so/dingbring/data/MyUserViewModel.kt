package com.so.dingbring.data

import android.content.Context
import android.graphics.drawable.Drawable
import android.provider.Settings.Global.getString
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.facebook.AccessToken
import com.so.dingbring.R
import com.so.dingbring.view.main.MainActivity
import kotlinx.android.synthetic.main.activity_main.*

class MyUserViewModel(private val mUserRepository: MyUserRepository): ViewModel() {

    fun createUser(mDataUser: MutableMap<String, Any>) { mUserRepository.createUser(mDataUser) }

    fun getUserById(mUserId : String) : LiveData<MyUser> { return mUserRepository.getUserById(mUserId) }

    fun getifNewUser(mUserId : String) : LiveData<Boolean>? { return mUserRepository.getifNewUser(mUserId) }

    fun updateUserName(mUserId:String, mUserName:String) { mUserRepository.updateUserName(mUserId,mUserName )  }

    fun updateUserPhoto(mUserId:String, mUserPhoto:String) { mUserRepository.updateUserPhoto(mUserId, mUserPhoto )  }

    fun upadateEventUser(mIDUser: String, mEventUniqueID: String) { mUserRepository.upadateEventUser(mIDUser, mEventUniqueID )  }


    fun createUserMain(
        displayName: String,
        email: String,
        mPhotoUser: String,
        currentUser: String,
        emptylist: ArrayList<String>,
        i: Int
    ) {

        val mDataUser: MutableMap<String, Any> = HashMap()
        mDataUser["NameUser"] = displayName
            mDataUser["EmailUser"] = email
            mDataUser["PhotoUser"] = mPhotoUser
            mDataUser["DocIdUser"] = currentUser
            mDataUser["eventUser"] = emptylist
        mDataUser["NbCreateEventUser"] = i
        mUserRepository.createUser(mDataUser) }

    fun Userv1(mPhotoUser: String) : String {
        var mImageUrl = ""
        val mPhotoUserSplit1 = mPhotoUser.split("//")
        val mPhotoUserSplit2 = mPhotoUserSplit1[1].split(".com/")
        val mPhotoUserSplit3 = mPhotoUserSplit2[0]
        if (mPhotoUserSplit3 == "graph.facebook") {
            val token = AccessToken.getCurrentAccessToken().token
             mImageUrl = "$mPhotoUser?access_token=$token" }
        else {mImageUrl = mPhotoUser}

        return mImageUrl }

    fun Userv2(mNbUser: Int, mContext: Context) : String {
        var mProfilStatus = ""
        if(mNbUser in 0..4)     { mProfilStatus = mContext.getString(R.string.newbie) }
        if(mNbUser in 5..9)     { mProfilStatus = mContext.getString(R.string.beginner) }
        if(mNbUser in 10..14)   { mProfilStatus = mContext.getString(R.string.intermediate) }
        if(mNbUser in 15..19)   { mProfilStatus = mContext.getString(R.string.experienced) }
        if(mNbUser in 20..24)   { mProfilStatus = mContext.getString(R.string.advanced) }
        if(mNbUser > 24)        { mProfilStatus = mContext.getString(R.string.expert) }
        return mProfilStatus }


    fun Userv3(mNbUser: Int, mContext: Context) : Drawable? {
        var mProfilStatusImg  = ContextCompat.getDrawable(mContext, R.drawable.medalone)

        if(mNbUser in 0..4)   { mProfilStatusImg = ContextCompat.getDrawable(mContext, R.drawable.medalone)!! }
        if(mNbUser in 5..9)   { mProfilStatusImg = ContextCompat.getDrawable(mContext, R.drawable.medaltwo)!! }
        if(mNbUser in 10..14) { mProfilStatusImg = ContextCompat.getDrawable(mContext, R.drawable.medalthree)!! }
        if(mNbUser in 15..19)   { mProfilStatusImg = ContextCompat.getDrawable(mContext, R.drawable.medalfour)!! }
        if(mNbUser in 20..24)   { mProfilStatusImg = ContextCompat.getDrawable(mContext, R.drawable.medalfive)!! }
        if(mNbUser > 24)   { mProfilStatusImg =ContextCompat.getDrawable(mContext, R.drawable.medalsix)!! }
        return mProfilStatusImg }


}