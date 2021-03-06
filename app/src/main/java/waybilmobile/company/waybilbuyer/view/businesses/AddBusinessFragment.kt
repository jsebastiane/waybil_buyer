package waybilmobile.company.waybilbuyer.view.businesses

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.GeoPoint
import kotlinx.android.synthetic.main.fragment_add_business.*
import waybilmobile.company.waybilbuyer.R
import waybilmobile.company.waybilbuyer.getCurrentLocation
import waybilmobile.company.waybilbuyer.resetTempGeoPoint
import waybilmobile.company.waybilbuyer.view.GeopinActivity
import waybilmobile.company.waybilbuyer.viewModel.businesses.AddBusinessViewModel


class AddBusinessFragment : Fragment() {

    private lateinit var viewModel: AddBusinessViewModel
    private var businessLocation: GeoPoint? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_business, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(AddBusinessViewModel::class.java)

        close_fragment_addBusiness.setOnClickListener {
            findNavController().navigateUp()
        }

        cancel_addBusiness.setOnClickListener {
            findNavController().navigateUp()
        }

        save_addBusiness.setOnClickListener {
            saveNewBusiness()
        }

        shareLocation_addBusiness.setOnClickListener {
            val lm = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            ) {
                locationSettingsPrompt()
            } else {
                startActivity(Intent(activity, GeopinActivity::class.java))
            }
        }

        observeViewModels()


    }

    private fun observeViewModels() {
        viewModel.loading.observe(viewLifecycleOwner, Observer { loading ->
            loading?.let {
                if(loading){
                    addBusiness_progressBar.visibility = View.VISIBLE
                }else{
                    addBusiness_progressBar.visibility = View.GONE
                }
            }
        })

        viewModel.uploadComplete.observe(viewLifecycleOwner, Observer { uploadComplete ->
            uploadComplete?.let {
                findNavController().navigateUp()
            }
        })

        viewModel.uploadSuccess.observe(viewLifecycleOwner, Observer { uploadSuccess ->
            uploadSuccess?.let {
                Toast.makeText(activity, getString(R.string.success), Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.uploadFailure.observe(viewLifecycleOwner, Observer { uploadFailure ->
            uploadFailure?.let {
                Toast.makeText(activity, getString(R.string.failed_to_create_business), Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun saveNewBusiness() {
        //Business ID is assigned during document creation
        val businessName = editBizName_addBusiness.text.toString()
        val businessAddress = editBizAddress_addBusiness.text.toString()
        val businessType = editBizType_addBusiness.text.toString()
        val businessPhoneNumber =  editBizPhone_addBusiness.text.toString()

        if (businessName.isEmpty()) {
            Toast.makeText(
                activity,
                getString(R.string.enter_business_name),
                Toast.LENGTH_SHORT
            )
                .show()
            return
        }

        if (businessAddress.isEmpty()) {
            Toast.makeText(
                activity,
                getString(R.string.enter_business_address),
                Toast.LENGTH_SHORT
            )
                .show()
            return
        }

        if (businessType.isEmpty()) {
            Toast.makeText(
                activity,
                getString(R.string.enter_business_type),
                Toast.LENGTH_SHORT
            )
                .show()
            return
        }

        if (businessPhoneNumber.isEmpty()) {
            Toast.makeText(
                activity,
                getString(R.string.enter_phone_number),
                Toast.LENGTH_SHORT
            )
                .show()
            return
        }


        if (businessLocation == null) {
            Toast.makeText(
                activity,
                getString(R.string.share_location),
                Toast.LENGTH_SHORT
            )
                .show()
            return
        }

        viewModel.submitNewBusiness(businessName, businessAddress, businessType, businessLocation!!,
            businessPhoneNumber)


    }

    override fun onResume() {
        super.onResume()
        businessLocation = getCurrentLocation()
        resetTempGeoPoint()
        if (businessLocation == null) {
            geolocation_status_addBusiness.setBackgroundResource(R.drawable.circle_status_pending)

        } else {
            geolocation_status_addBusiness.setBackgroundResource(R.drawable.circle_status_confirmed)

        }
    }

    private fun locationSettingsPrompt() {
        activity?.let{
            val builder = AlertDialog.Builder(it)
            builder.setMessage(getString(R.string.location_permission_query))
                .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }.setPositiveButton(getString(R.string.yes)) { _, _ ->
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)

                }


            val dialog = builder.create()
            dialog.show()
            dialog.show()
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
                ContextCompat.getColor(
                    it,
                    R.color.colorAccent
                )
            )
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
                ContextCompat.getColor(
                    it,
                    R.color.colorAccent
                )
            )
        }


    }


}