package com.o7solutions.firebase

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import com.o7solutions.ClickInterface
import com.o7solutions.adapter.MenuAdapter
import com.o7solutions.firebase.databinding.DialogAddUpdateMenuBinding
import com.o7solutions.firebase.databinding.FragmentListBinding
import com.o7solutions.firebase.model.MenuModel
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ListFragment : Fragment(), ClickInterface {

    lateinit var binding: FragmentListBinding
    lateinit var mainActivity: MainActivity
    lateinit var fcbAdd: FloatingActionButton
    lateinit var menuAdapter: MenuAdapter
    lateinit var dbReference: DatabaseReference
    lateinit var tvText: TextView
    lateinit var recyclerView: RecyclerView
    var userList=ArrayList<MenuModel>()
    private var param1: String? = null
    private var param2: String? = null
    private val TAG = "ListFragment"
    override fun onCreate(savedInstanceState: Bundle?) {
        mainActivity = activity as MainActivity
        super.onCreate(savedInstanceState)
        dbReference = FirebaseDatabase.getInstance().reference
        arguments?.let {
        }
        dbReference.addChildEventListener(object:  ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                Log.e(TAG," snapshot ${snapshot.value}")
                val menuModel: MenuModel? = snapshot.getValue(MenuModel::class.java)
                menuModel?.id = snapshot.key
                if (menuModel != null) {
                    userList.add(menuModel)
                }
                    menuAdapter.notifyDataSetChanged()
                Log.e(TAG,"menu model ${menuModel?.name}")
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                Log.e(TAG," on child changed")
            }
            override fun onChildRemoved(snapshot: DataSnapshot){
                Log.e(TAG," on child removed")
            }
            override fun onChildMoved(snapshot: DataSnapshot,previousChildName: String?) {
                Log.e(TAG," on child moved")
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        menuAdapter = MenuAdapter(userList,this)
        recyclerView.layoutManager = LinearLayoutManager(mainActivity)
        recyclerView.adapter=menuAdapter


        fcbAdd = view.findViewById(R.id.fcbAdd)
        fcbAdd.setOnClickListener {
            val binding = DialogAddUpdateMenuBinding.inflate(layoutInflater)
            val addDialog = Dialog(mainActivity)
            addDialog.setContentView(binding.root)
            binding.etMenuName= v.findViewById(R.id.etMenuName)
            etMenuPrice = v.findViewById(R.id.etMenuPrice)
            btnAdd = v.findViewById(R.id.btnAdd)
            btnCancel = v.findViewById(R.id.btnCancel)
            btnAdd.setOnClickListener {
                val menuData = MenuModel(etMenuName.text.toString(), etMenuPrice.text.toString())
                if (etMenuName.text.isEmpty()) {
                    etMenuName.setError("Enter Name")
                } else if (etMenuPrice.text.isEmpty()) {
                    etMenuPrice.setError("Enter Price")
                } else {
                    dbReference.push().setValue(menuData)
                        .addOnCompleteListener {
                            Toast.makeText(requireContext(), "Menu Add", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener { err ->
                            Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
                        }
                    addDialog.dismiss()
                }
            }
            btnCancel.setOnClickListener {
                addDialog.dismiss()
                Toast.makeText(mainActivity, "Cancel", Toast.LENGTH_SHORT).show()
            }
            addDialog.create()
            addDialog.show()
        }
      return view
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
       }
    override fun editClick(menuModel: MenuModel, position: Int) {
        val binding = DialogAddUpdateMenuBinding.inflate(layoutInflater)
        val editDialog = Dialog(mainActivity)
        etMenuName = v.findViewById<EditText>(R.id.etMenuName)
        etMenuPrice = v.findViewById(R.id.etMenuPrice)
        btnAdd = v.findViewById(R.id.btnAdd)
        tvText = v.findViewById(R.id.tvText)
        btnCancel = v.findViewById(R.id.btnCancel)
        etMenuName.setText(menuModel.name)
        etMenuPrice.setText(menuModel.price)
        tvText.setText("Update Item")

        btnAdd.setText("Update")

        btnAdd.setOnClickListener {
            val menuData = MenuModel(etMenuName.text.toString(), etMenuPrice.text.toString())
            if (etMenuName.text.isEmpty()) {
                etMenuName.setError("Enter Name")
            } else if (etMenuPrice.text.isEmpty()) {
                etMenuPrice.setError("Enter Price")
            } else {
                val key = menuModel.id
                val post = MenuModel("", etMenuName.text.toString(), etMenuPrice.text.toString())
                val postValues = post.toMap()

                val childUpdates = hashMapOf<String, Any>(
                    "$key" to postValues,
                )

                dbReference.updateChildren(childUpdates)
                editDialog.dismiss()
            }
            }
            btnCancel.setOnClickListener {
                editDialog.dismiss()
                Toast.makeText(mainActivity, "Cancel", Toast.LENGTH_SHORT).show()
            }
        editDialog.show()
        }

    override fun deleteClick(menuModel: MenuModel, position: Int) {
        val dialog= AlertDialog.Builder(context)
        dialog.setTitle("Delete")
        dialog.setMessage("Are you sure...")
        dialog.setPositiveButton("Yes"){addDialog, _ ->
            dbReference.child(menuModel.id ?: "").removeValue()
            addDialog.dismiss()
        }
        dialog.setNegativeButton("No"){addDialog, _ ->
            addDialog.dismiss()
        }
        dialog.create()
        dialog.show()
        }
    }



