package com.davidsadler.bluepail.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.TimePicker
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.davidsadler.bluepail.R
import com.davidsadler.bluepail.adapters.ColorsAdapter
import com.davidsadler.bluepail.adapters.OnColorSelectedListener
import com.davidsadler.bluepail.util.*
import kotlinx.android.synthetic.main.fragment_plant_detail.*
import com.davidsadler.bluepail.viewModels.PlantCreationViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class PlantDetail : Fragment(), OnColorSelectedListener, OnReminderUpdatedListener, TimePicker.OnTimeChangedListener {

    private val args: PlantDetailArgs by navArgs()
    private lateinit var viewModel: PlantCreationViewModel
    private lateinit var colorsAdapter: ColorsAdapter

    override fun onTimeChanged(view: TimePicker?, hourOfDay: Int, minute: Int) {
        view?.let { timePicker ->
            val timePickerTag = timePicker.tag
            if (timePickerTag is Int) {
                when (timePickerTag) {
                    0 -> {
                        viewModel.setWateringTime(hourOfDay,minute)
                        updateWateringReminderElements()
                    }
                    1 -> {
                        viewModel.setFertilizingTime(hourOfDay,minute)
                        updateFertilizingReminderElements()
                    }
                }
            }
        }
    }

    override fun onReminderUpdated(next: Date, interval: Int, isForFertilizing: Boolean) {
        if (isForFertilizing) {
            viewModel.setFertilizingDate(next)
            viewModel.setFertilizingInterval(interval)
            updateFertilizingReminderElements()
        } else {
            viewModel.setWateringDate(next)
            viewModel.setWateringInterval(interval)
            updateWateringReminderElements()
        }
    }

    override fun colorSelected(colorId: Int) {
        viewModel.setColorId(colorId)
        updateColorRecyclerView()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(PlantCreationViewModel::class.java)
         return inflater.inflate(R.layout.fragment_plant_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inflateBottomToolbar()
        setupToolbarItems()
        setupColorRecyclerView()
        setupReminderClickListeners()
        setupTimePickers()
        setupPhotoImageButton()
        checkIfPlantWasSelected()
    }

    override fun onResume() {
        super.onResume()
        activity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED
    }

    override fun onDestroy() {
        super.onDestroy()
        activity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_USER
    }

    private fun inflateBottomToolbar() {
        toolbar_cancel_save.inflateMenu(R.menu.detail_toolbar)
    }

    private fun setupToolbarItems() {
        toolbar_cancel_save.menu.findItem(R.id.menu_item_cancel).setOnMenuItemClickListener {
            navigateToPlantList()
            true
        }
        toolbar_cancel_save.menu.findItem(R.id.menu_item_save).setOnMenuItemClickListener {
            savePlant()
            true
        }
    }

    private fun setupColorRecyclerView() {
        this.context?.let { context ->
            recyclerView_plant_colors.layoutManager = GridLayoutManager(context,1,GridLayoutManager.HORIZONTAL,false)
            colorsAdapter = ColorsAdapter(context,this)
            recyclerView_plant_colors.adapter = colorsAdapter
        }
    }

    private fun setupReminderClickListeners() {
        this.view?.let {
            imageButton_setup_watering.setOnClickListener {
                val setupDialog = PlantReminderSetupDialog(this,false)
                setupDialog.show(this.activity!!.supportFragmentManager,"reminder_dialog")
            }
            imageButton_setup_fertilizing.setOnClickListener{
                val setupDialog = PlantReminderSetupDialog(this,true)
                setupDialog.show(this.activity!!.supportFragmentManager,"reminder_dialog")
            }
        }
    }

    private fun setupTimePickers() {
        timePicker_watering_time.setOnTimeChangedListener(this)
        timePicker_watering_time.tag = 0
        timePicker_setup_fertilizing.setOnTimeChangedListener(this)
        timePicker_setup_fertilizing.tag = 1
    }

    private fun setupPhotoImageButton() {
        imageButton_plant_photo.setOnClickListener{
            dispatchTakePictureIntent()
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir = this.activity!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_",".jpg",storageDir).apply {
            viewModel.setPhotoUri(absolutePath)
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(this.activity!!.packageManager)?.also {
                val photoFile: File? = try {
                    print("Creating Image File")
                    createImageFile()
                } catch (ex: IOException) {
                    println("ERROR: Error occurred while creating file. ${ex.localizedMessage}")
                    null
                }
                photoFile?.also {
                    val photoURI = FileProvider.getUriForFile(this.context!!, "com.example.android.fileprovider",it)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            updatePhotoImageButton()
        } else {
            Toast.makeText(this.context!!,"Plant photo not saved",Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToPlantList() {
        this.view?.let {
            hideKeyboard()
            val action = PlantDetailDirections.actionPlantDetailToPlantList()
            Navigation.findNavController(it).navigate(action)
        }
    }

    private fun savePlant() {
        println("Save plant pressed")
        if (allRequiredParametersAreSet()) {
            viewModel.savePlant()
            scheduleAlarmNotifications()
            navigateToPlantList()
        }
    }

    private fun allRequiredParametersAreSet(): Boolean {
        this.context?.let {context ->
            if (editText_plant_name.text.toString().isEmpty()) {
                Toast.makeText(context,"Please enter a plant name",Toast.LENGTH_SHORT).show()
                return false
            } else {
                viewModel.setPlantName(editText_plant_name.text.toString())
            }
            if (viewModel.getWateringDate() == null) {
                Toast.makeText(context,"Please enter a watering schedule",Toast.LENGTH_SHORT).show()
                return false
            }
        }
        return true
    }

    private fun updateNameEditText() {
        editText_plant_name.setText(viewModel.getName())
    }

    private fun updateColorRecyclerView() {
        colorsAdapter.selectedColor = viewModel.getColorId()
        colorsAdapter.notifyDataSetChanged()
    }

    private fun updateWateringReminderElements() {
        if (viewModel.getWateringDate() != null) {
            textView_next_watering_reminder.text = viewModel.getWateringDate()!!.toShortString(true)
            textView_watering_interval.text = viewModel.getReminderIntervalInDays(true)
            if (!timePicker_watering_time.isVisible) {
                timePicker_watering_time.isVisible = true
            }
//            if (Build.VERSION.SDK_INT >= 23) {
//                timePicker_watering_time.hour = viewModel.getWateringDate()!!.getHour(false)
//                timePicker_watering_time.minute = viewModel.getWateringDate()!!.getMinute()
//            }
        }
    }

    private fun updateFertilizingReminderElements() {
        if (viewModel.getFertilizingDate() != null) {
            textView_next_fertilizing.text = viewModel.getFertilizingDate()!!.toShortString(true)
            textView_fertilizing_interval.text = viewModel.getReminderIntervalInDays(false)
            if (!timePicker_setup_fertilizing.isVisible) {
                timePicker_setup_fertilizing.isVisible = true
            }
//            if (Build.VERSION.SDK_INT >= 23) {
//                timePicker_setup_fertilizing.hour = viewModel.getFertilizingDate()!!.getHour(false)
//                timePicker_setup_fertilizing.minute = viewModel.getFertilizingDate()!!.getMinute()
//            }
        }
    }

    private fun updatePhotoImageButton() {
        val photoFilePath = viewModel.getPhotoUri()
        if (photoFilePath != null) {
            val photoUri = Uri.parse("file://$photoFilePath")
            val picasso = Picasso.get()
                picasso
                    .load(photoUri).resize(250,250)
                    .centerInside()
                    .placeholder(R.drawable.image_button_photo)
                    .into(imageButton_plant_photo)
        }
    }

    private fun checkIfPlantWasSelected() {
        if (args.plantId != 0) {
            if (viewModel.getId() == 0) {
                viewModel.findById(args.plantId).observe(viewLifecycleOwner, androidx.lifecycle.Observer { plant ->
                    this.activity!!.toolbar.setTitle(R.string.toolbar_title_edit_your_plant)
                    viewModel.setPlantName(plant.name)
                    viewModel.setPlantId(plant.id)
                    viewModel.setColorId(plant.colorId)
                    viewModel.setWateringDate(plant.wateringDate)
                    viewModel.setWateringInterval(plant.daysBetweenWatering)
                    viewModel.setFertilizingDate(plant.fertilizerDate)
                    viewModel.setFertilizingInterval(plant.daysBetweenFertilizing)
                    viewModel.setPhotoUri(plant.photo)
                    updateNameEditText()
                    updateColorRecyclerView()
                    updateWateringReminderElements()
                    updateFertilizingReminderElements()
                    updatePhotoImageButton()
                })
            }
        }
    }

    private fun scheduleAlarmNotifications() {
        this.context?.let {
            AlarmNotificationManager.scheduleNotificationAlarm(viewModel.getName(),
                viewModel.getId()
                ,true,
                viewModel.getWateringDate()!!,
                it)
            if (viewModel.getFertilizingDate() != null) {
                AlarmNotificationManager.scheduleNotificationAlarm(viewModel.getName(),
                    viewModel.getId(),
                    false,
                    viewModel.getFertilizingDate()!!,
                    it)
            }
        }
    }
}

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}
