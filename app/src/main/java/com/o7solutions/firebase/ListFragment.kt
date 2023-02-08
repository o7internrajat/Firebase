package com.o7solutions.firebase

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.o7solutions.ClickInterface
import com.o7solutions.adapter.MenuAdapter
import com.o7solutions.firebase.databinding.DialogAddUpdateMenuBinding
import com.o7solutions.firebase.databinding.FragmentListBinding
import com.o7solutions.firebase.model.MenuModel
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
    lateinit var menuAdapter: MenuAdapter
    lateinit var dbReference: DatabaseReference
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
                    menuAdapter.notifyDataSetChanged()
                }
                Log.e(TAG,"menu model ${menuModel?.name}")
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                Log.e(TAG," on child changed")
                val menuModel:MenuModel?= snapshot.getValue(MenuModel::class.java)
                menuModel?.id=snapshot.key
                if (menuModel!=null) {
                    userList.forEachIndexed { index, menuModelData ->
                        if(menuModelData.id == menuModel.id) {
                            userList[index] = menuModel
                            return@forEachIndexed
                        }
                    }
                    menuAdapter.notifyDataSetChanged()
                }
            }
            override fun onChildRemoved(snapshot: DataSnapshot) {
                Log.e(TAG," on child removed")
                val menuModel:MenuModel?= snapshot.getValue(MenuModel::class.java)
                menuModel?.id=snapshot.key
                if(menuModel!=null){
                    userList.remove(menuModel)
                    menuAdapter.notifyDataSetChanged()
                }
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
        binding= FragmentListBinding.inflate(layoutInflater)
        menuAdapter = MenuAdapter(userList,this)
       binding.recyclerView.layoutManager = LinearLayoutManager(mainActivity)
        binding.recyclerView.adapter=menuAdapter
        binding.fcbAdd.setOnClickListener {
            val binding = DialogAddUpdateMenuBinding.inflate(layoutInflater)
            val addDialog = Dialog(mainActivity)
            addDialog.setContentView(binding.root)
           binding.btnAdd.setOnClickListener {
                val menuData = MenuModel("",binding.etMenuName.text.toString(), binding.etMenuPrice.text.toString())
                if (binding.etMenuName.text.isEmpty()) {
                    binding.etMenuName.setError("Enter Name")
                } else if (binding.etMenuPrice.text.isEmpty()) {
                    binding.etMenuPrice.setError("Enter Price")
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
            binding.btnCancel.setOnClickListener {
                addDialog.dismiss()
                Toast.makeText(mainActivity, "Cancel", Toast.LENGTH_SHORT).show()
            }
            addDialog.create()
            addDialog.show()
        }
      return binding.root
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
        binding.tvText.setText("Update Item")
        binding.btnAdd.setText("Update")
        binding.etMenuName.setText(menuModel.name)
        binding.etMenuPrice.setText(menuModel.price)
        editDialog.setContentView(binding.root)
        editDialog.create()
        editDialog.show()
        binding.btnAdd.setOnClickListener {
            if (binding.etMenuName.text.isEmpty()) {
                binding.etMenuName.setError("Enter Name")
            } else if (binding.etMenuPrice.text.isEmpty()) {
                binding.etMenuPrice.setError("Enter Price")
            } else {
                val key = menuModel.id
                val post = MenuModel("",binding.etMenuName.text.toString(), binding.etMenuPrice.text.toString())
                val postValues = post.toMap()
                val childUpdates = hashMapOf<String, Any>(
                    "$key" to postValues,
                )
                dbReference.updateChildren(childUpdates)
                editDialog.dismiss()
            }
        }
            binding.btnCancel.setOnClickListener {
                editDialog.dismiss()
                Toast.makeText(mainActivity, "Cancel", Toast.LENGTH_SHORT).show()
            }
        }
    override fun deleteClick(menuModel: MenuModel, position: Int) {
        val dialog= AlertDialog.Builder(context)
        dialog.setTitle("Delete")
        dialog.setMessage("Are you sure...")
        dialog.setPositiveButton("Yes"){addDialog, _ ->
            dbReference.child(menuModel.id ?: "").removeValue()
        }
        dialog.setNegativeButton("No"){addDialog, _ ->
            addDialog.dismiss()
        }
        dialog.create()
        dialog.show()
        }
}



