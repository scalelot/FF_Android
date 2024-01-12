package com.festum.festumfield.verstion.firstmodule.screens.fragment

import android.R.attr
import android.R.attr.phoneNumber
import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import com.festum.festumfield.databinding.FragmentPhoneContactBinding
import com.festum.festumfield.verstion.firstmodule.screens.BaseFragment
import com.festum.festumfield.verstion.firstmodule.screens.adapters.ContactListAdapter
import com.festum.festumfield.verstion.firstmodule.sources.local.model.PhoneContactList
import com.festum.festumfield.verstion.firstmodule.utils.DeviceUtils
import com.festum.festumfield.verstion.firstmodule.viemodels.ChatViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale


@AndroidEntryPoint
class PhoneContactFragment : BaseFragment<ChatViewModel>() {

    private lateinit var binding: FragmentPhoneContactBinding
    private var contactListAdapter: ContactListAdapter? = null

    private val contactArrayList = arrayListOf<PhoneContactList?>()
    private val phonebookList = arrayListOf<PhoneContactList?>()
    private val removeDuplicatesList = arrayListOf<PhoneContactList?>()

    private val nameList = arrayListOf<String>()
    private val isFFUser = arrayListOf<Boolean>()
    private val numberList = arrayListOf<String>()

    override fun getContentView(): View {
        binding = FragmentPhoneContactBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun initUi() {


        binding.idPBLoading.visibility = View.VISIBLE

        val contactList = arrayListOf<String?>()

        getContacts().forEach {

            contactList.add(it?.number.toString().replace("+",""))

        }

        viewModel.getPhonebook(contactList)


        binding.edtSearchText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().trim { it <= ' ' }.isEmpty()) {
                    binding.ivClearText.visibility = View.GONE
                    binding.ivSearch.visibility = View.VISIBLE
                } else {
                    binding.ivClearText.visibility = View.VISIBLE
                    binding.ivSearch.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable) {
                val text = s.toString()
                filter(text)
                binding.ivSearch.visibility = View.GONE
                binding.ivClearText.visibility = View.VISIBLE
            }
        })

        binding.ivClearText.setOnClickListener(View.OnClickListener {
            binding.edtSearchText.setText("")
            binding.ivClearText.visibility = View.GONE
            binding.ivSearch.visibility = View.VISIBLE
            DeviceUtils.hideKeyboard(requireActivity())
        })

    }

    override fun setObservers() {

        viewModel.phonebookData.observe(viewLifecycleOwner){ phonebookData ->

            phonebookData?.forEach {

                numberList.add(it.contactNo.toString())
                it.isFFuser?.let { it1 -> isFFUser.add(it1) }

            }

            Handler(Looper.getMainLooper()).postDelayed({

                for (i in 0 until minOf(nameList.size, minOf(numberList.size, isFFUser.size))) {
                    val contact = PhoneContactList(nameList[i], numberList[i], isFFUser[i])
                    contactArrayList.add(contact)
                }

                val reorderedList = contactArrayList.sortedByDescending { it?.isFFuser }

                phonebookList.addAll(reorderedList)



            }, 500)


            Handler(Looper.getMainLooper()).postDelayed({

                contactListAdapter = ContactListAdapter(requireActivity(), phonebookList )

                binding.recyclerviewContact.adapter = contactListAdapter

                binding.idPBLoading.visibility = View.GONE

            }, 1000)



        }

    }

    @SuppressLint("Range")
    private fun getContacts(): ArrayList<PhoneContactList?> {

        val contactsList = ArrayList<PhoneContactList?>()
        val mobileNoSet = HashSet<String>()

        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        val cursor = requireActivity().contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            null,
            null,
            null
        )

        cursor?.use {
            while (it.moveToNext()) {
                val name =
                    it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val number =
                    it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

                if (!mobileNoSet.contains(name)) {

                    contactsList.add(PhoneContactList(name = name, number = number))
                    mobileNoSet.add(name)
                    nameList.add(name)

                }


            }
        }

        cursor?.close()

        return contactsList
    }

    private fun filter(text: String) {

        val filteredList = ArrayList<PhoneContactList?>()

        if (getContacts().isNotEmpty()) {

            getContacts().forEach { item ->
                if (item?.name?.lowercase(Locale.getDefault())?.contains(text.lowercase(Locale.getDefault())) == true
                ) {
                    filteredList.add(item)
                }
            }

            if (filteredList.isEmpty()) {
                contactListAdapter?.filterList(filteredList)
                Toast.makeText(context, "No User Found..", Toast.LENGTH_SHORT).show()
            } else {
                contactListAdapter?.filterList(filteredList)
            }

        }

    }

    /*private fun removeDuplicateContacts(contactsList: ArrayList<PhoneContactList?>): ArrayList<PhoneContactList?> {
        return contactsList
            .distinctBy { it?.name to it?.number }
            .toArrayList()
    }*/

}