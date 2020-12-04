package com.so.dingbring.view.detail

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.gauravk.bubblenavigation.BubbleNavigationLinearView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLink
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.so.dingbring.R
import com.so.dingbring.data.MyEventViewModel
import com.so.dingbring.data.MyItem
import com.so.dingbring.data.MyItemViewModel
import com.so.dingbring.view.base.BaseFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.dialog_layout_calendar.*
import kotlinx.android.synthetic.main.dialog_layout_detail.*
import kotlinx.android.synthetic.main.dialog_layout_detail_info.*
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.android.synthetic.main.item_main.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*


class DetailFragment : BaseFragment() {

    var mEventId = ""
    var mEventName = ""
    private lateinit var mDetailAdapter: DetailAdapter
    private val mItemVM by viewModel<MyItemViewModel>()
    private val mEventVM by viewModel<MyEventViewModel>()
    var mItemStatus: String = "I bring"
    var mItemName: String = "<3"
    var mItemUniqueID = UUID.randomUUID().toString()
    var mListMyItem: ArrayList<ArrayList<String>> = arrayListOf()
    var mNameUser = "..."
    var mIdUser  = FirebaseAuth.getInstance().currentUser?.uid.toString()


    var mBubble : BubbleNavigationLinearView? = null
    var mTopBarTxt : TextView? = null

    var mFloat_back : FloatingActionButton? = null
    var mFloat_action : FloatingActionButton? = null


    var mItemQuantity = 1
    lateinit var d_detail:Dialog
    private var thisView: View? = null

     var mTextName: TextView? = null



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mEventId = arguments?.get("GlobalIdEvent").toString()
        initTopBottomBar()
        thisView =  inflater.inflate(R.layout.fragment_detail, container, false)
        return thisView
    }

    private fun initTopBottomBar() {
        mBubble = activity?.findViewById(R.id.float_bottom_bar)
        mBubble!!.visibility = View.INVISIBLE
        // mBubble!!.setCurrentActiveItem(1)

        mTopBarTxt = activity?.findViewById(R.id.item_tool_bar)
        mTopBarTxt?.text  = "Detail"
        mTopBarTxt?.setTextColor(resources.getColor(R.color.red_300))


        mFloat_back = activity?.findViewById(R.id.item_tb_fb_back)
        mFloat_back?.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.red_300))
        mFloat_back?.setColorFilter(Color.argb(255, 255, 255, 255))


        mFloat_action = activity?.findViewById(R.id.item_tb_fb_action)
        mFloat_action?.visibility = View.VISIBLE
        mFloat_action?.setImageResource(R.drawable.logo_share)
        mFloat_action?.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.red_300))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mTextName = view?.findViewById(R.id.detail_name_txt)
        initCreateItem()
        initRetrieveItem()
        prepareToShare()
        onBackPressed()
        onBackBarPressed()
        initHeader()
    }

    private fun onBackPressed() {
        requireActivity().onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() { navToHome() } })
    }
    private fun onBackBarPressed() {
        mFloat_back = activity?.findViewById(R.id.item_tb_fb_back)
        mFloat_back?.setOnClickListener {
            navToHome()
        }
    }
    private fun navToHome() {
        Navigation.findNavController(requireActivity(), R.id.hostFragment).navigate(R.id.event_fragment)
        mTopBarTxt?.text  = "Event"
        mBubble!!.visibility = View.VISIBLE
        mBubble?.setCurrentActiveItem(1)
    }


    private fun prepareToShare() {
        mFloat_action?.setOnClickListener {
            val dynamicLink = Firebase.dynamicLinks.dynamicLink {
                link = Uri.parse(   "https://dingbring.page.link/" + mEventId )
                domainUriPrefix = "https://dingbring.page.link/"
                androidParameters { } }

            val dynamicLinkUri = dynamicLink.uri
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, dynamicLinkUri.toString())
            startActivity(Intent.createChooser(intent, "Share Link")) }
    }

    @SuppressLint("CheckResult")
    private fun initRetrieveItem() {
        mDetailAdapter = DetailAdapter(requireContext(), mListMyItem)
        detail_recyclerView.layoutManager = LinearLayoutManager(context)
        detail_recyclerView.adapter = mDetailAdapter
        initRVObserver()
        mDetailAdapter.itemClickN.subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread())
                .subscribe { data ->

                    if (data.containsKey(0)) {
                        d_detail= Dialog(requireContext())
                        d_detail.requestWindowFeature(Window.FEATURE_NO_TITLE)
                        d_detail.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        d_detail.setContentView(R.layout.dialog_layout_detail)
                        d_detail.show()

                        d_detail.dialog_up.setOnClickListener { mItemVM.updateStatus(data)
                            initRVObserver()
                            d_detail.dismiss()}

                        d_detail.dialog_down.setOnClickListener { d_detail.dismiss()}


                    }
                    else { mItemVM.updateStatus(data)
                    initRVObserver()  }} }




    private fun initRVObserver() {
        mItemVM.getItem(mEventId).observe(requireActivity(), androidx.lifecycle.Observer {mlmi ->


            mlmi.sortByDescending { it[1] }
            mListMyItem.clear()
            mListMyItem.addAll(mlmi)
            mDetailAdapter.notifyDataSetChanged()})
    }


    private fun initHeader() {
        mEventVM.getEventrById(mEventId).observe(requireActivity(), androidx.lifecycle.Observer { myevent ->
            var mEventAdress = myevent.mEventAdress
            var mEventDate = myevent.mEventDate
            var mEventHour = myevent.mEventHour
            var mEventName = myevent.mEventName
            var mEventDesc = myevent.mEventDesc

            mTextName!!.text = mEventName
            detail_button_info.setOnClickListener {

                val d = Dialog(requireContext())
                d.requestWindowFeature(Window.FEATURE_NO_TITLE)
                d.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                d.setContentView(R.layout.dialog_layout_detail_info)
                d.dialog_det_info_addressevent.text = mEventAdress
                d.dialog_det_info_dateevent.text = mEventDate
                d.dialog_det_info_nameevent.text = mEventName
                d.dialog_det_info_hourevent.text = mEventHour
                d.dialog_det_info_descevent.text = mEventDesc
                d.dialog_det_info_cancel.setOnClickListener { d.dismiss() }
                d.show()

            }




        }) }

    private fun initButton() {
        detail_button_add.setOnClickListener {
            goAnimTxt(detail_status_need)
            goAnimTxt(detail_status_bring) } }

    private fun initStatus() {
        detail_status_bring?.setOnClickListener {
            detail_status_bring_deco.visibility = View.VISIBLE
            mItemStatus = "I bring"
            detail_status_need_deco.visibility = View.INVISIBLE
            initAnimafterClickAdd()}

            detail_status_need?.setOnClickListener {
            detail_status_need_deco.visibility = View.VISIBLE
            mItemStatus = "I need"
            detail_status_bring_deco.visibility = View.INVISIBLE
                initAnimafterClickAdd()}}

    private fun initAnimafterClickAdd() {
        goAnimTxtLayout(detail_item_txt)
        goAnimEdit(detail_item_edit)
    }

    private fun initItem() {
        detail_item_edit.doOnTextChanged { text, start, before, count ->
            if (start == 1) {
                goAnimTxt(detail_qty_txt)
                goAnimView(detail_quantity_item)
                goAnimView(detail_quantity_minus)
                goAnimView(detail_quantity_plus)
                goAnimFloating(detail_check)
            }

            mItemName= detail_item_edit.text.toString()
        } }

    private fun initQuantity() {
        detail_quantity_txt.text =  mItemQuantity.toString()

        detail_quantity_minus.setOnClickListener {

            if (mItemQuantity > 1){
                mItemQuantity = mItemQuantity - 1
                detail_quantity_txt.text = mItemQuantity.toString()} }

        detail_quantity_plus .setOnClickListener {
            mItemQuantity = mItemQuantity + 1
             detail_quantity_txt.text = mItemQuantity.toString()} }

    private fun initCreateItem() {
        initButton()
        initItem()
        initQuantity()
        initStatus()

        detail_check?.setOnClickListener {
            initItem()
            if (mItemName == "") { Toast.makeText(requireContext(), "Please add item <3", Toast.LENGTH_LONG).show() }
            else { val mMyItem = MyItem(mItemUniqueID, mItemName, mItemStatus, mItemQuantity.toString(), mIdUser, mEventId)

               mItemVM.createUniqueItem(mMyItem)
                initRVObserver()
                invisible()

            }

        } }
    private fun invisible() {
        detail_status_need.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.fui_transparent))
        detail_status_bring.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.fui_transparent))
        detail_quantity_txt.text = "1"
        detail_item_edit.setText(" ")
        goAnimTxtBack(detail_status_need)
        goAnimTxtBack(detail_status_bring)
        goAnimTxtLayoutBack(detail_item_txt)
        goAnimEditBack(detail_item_edit)
        goAnimTxtBack(detail_qty_txt)
        goAnimImageViewBack(detail_status_bring_deco)
        goAnimImageViewBack(detail_status_need_deco)
        goAnimViewBack(detail_quantity_item)
        goAnimViewBack(detail_quantity_minus)
        goAnimViewBack(detail_quantity_plus)
        goAnimFloatingBack(detail_check) }

    private fun goAnimView(mLink: View?) {
        val zoom1 = AnimationUtils.loadAnimation(requireContext(), R.anim.zoomin_1)
        val zoom2 = AnimationUtils.loadAnimation(requireContext(), R.anim.zoomout_1)
        mLink?.startAnimation(zoom1)
        mLink?.startAnimation(zoom2) }

    private fun goAnimTxt(mLink: TextView?) {
        val zoom1 = AnimationUtils.loadAnimation(requireContext(), R.anim.zoomin_1)
        val zoom2 = AnimationUtils.loadAnimation(requireContext(), R.anim.zoomout_1)
        mLink?.startAnimation(zoom1)
        mLink?.startAnimation(zoom2) }

    private fun goAnimTxtLayout(mLink: TextInputLayout?) {
        val zoom1 = AnimationUtils.loadAnimation(requireContext(), R.anim.zoomin_1)
        val zoom2 = AnimationUtils.loadAnimation(requireContext(), R.anim.zoomout_1)
        mLink?.startAnimation(zoom1)
        mLink?.startAnimation(zoom2) }

    private fun goAnimEdit(mLink: EditText?) {
        val zoom1 = AnimationUtils.loadAnimation(requireContext(), R.anim.zoomin_1)
        val zoom2 = AnimationUtils.loadAnimation(requireContext(), R.anim.zoomout_1)
        mLink?.startAnimation(zoom1)
        mLink?.startAnimation(zoom2) }

    private fun goAnimFloating(mLink: FloatingActionButton?) {
        val zoom1 = AnimationUtils.loadAnimation(requireContext(), R.anim.zoomin_1)
        val zoom2 = AnimationUtils.loadAnimation(requireContext(), R.anim.zoomout_1)
        mLink?.startAnimation(zoom1)
        mLink?.startAnimation(zoom2) }

    private fun goAnimImageViewBack(mLink: ImageView?) {
        val zoom1 = AnimationUtils.loadAnimation(requireContext(), R.anim.zoomin_2)
        mLink?.startAnimation(zoom1) }

    private fun goAnimViewBack(mLink: View?) {
        val zoom1 = AnimationUtils.loadAnimation(requireContext(), R.anim.zoomin_2)
        mLink?.startAnimation(zoom1) }

    private fun goAnimTxtBack(mLink: TextView?) {
        val zoom1 = AnimationUtils.loadAnimation(requireContext(), R.anim.zoomin_2)
        mLink?.startAnimation(zoom1) }

    private fun goAnimTxtLayoutBack(mLink: TextInputLayout?) {
        val zoom1 = AnimationUtils.loadAnimation(requireContext(), R.anim.zoomin_2)
        mLink?.startAnimation(zoom1) }

    private fun goAnimEditBack(mLink: EditText?) {
        val zoom1 = AnimationUtils.loadAnimation(requireContext(), R.anim.zoomin_2)
        mLink?.startAnimation(zoom1) }

    private fun goAnimFloatingBack(mLink: FloatingActionButton?) {
        val zoom1 = AnimationUtils.loadAnimation(requireContext(), R.anim.zoomin_2)
        mLink?.startAnimation(zoom1) }



}
