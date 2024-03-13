package uz.coder.yandexmapkit

import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PointF
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CameraUpdateReason
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.RotationType
import com.yandex.mapkit.map.VisibleRegionUtils
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManager
import com.yandex.mapkit.search.SearchManagerType
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.Session
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider
import com.yandex.runtime.network.NetworkError
import com.yandex.runtime.network.RemoteError
import uz.coder.yandexmapkit.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(),UserLocationObjectListener,Session.SearchListener,CameraListener {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    /*********search*********/
    private lateinit var locationMapKit:UserLocationLayer
    private lateinit var searchManager: SearchManager
    private lateinit var searchSession: Session
    private fun submitQuery(query:String){
        searchSession = searchManager.submit(query,
            VisibleRegionUtils.toPolygon(binding.mapview.map.visibleRegion),
            SearchOptions(),this
        )
    }
    /*********search*********/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey("bc6381cb-3737-4bf1-82d3-9c0cbf47c7ec")
        MapKitFactory.initialize(this@MainActivity)
        setContentView(binding.root)
        binding.mapview.map.move(
            CameraPosition(
                Point(
                    41.589938, 60.608070),
            11.0f,0.0f,0.0f),
            Animation(Animation.Type.SMOOTH,10f),null
        )
        val instance = MapKitFactory.getInstance()
        requestPermission()
        val trafficLayer = instance.createTrafficLayer(binding.mapview.mapWindow)
        var trafficBoolean = false
        binding.traffic.setOnClickListener {
            trafficBoolean = !trafficBoolean
            trafficLayer.isTrafficVisible = trafficBoolean
        }
/***************************search***************************************/
        locationMapKit = instance.createUserLocationLayer(binding.mapview.mapWindow)
        locationMapKit.isVisible = true
        locationMapKit.setObjectListener(this@MainActivity)
        SearchFactory.initialize(this)
        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
        binding.mapview.map.addCameraListener(this)
        binding.search.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                submitQuery(binding.search.text.toString())
            }
            false
        }
        /***************************search***************************************/
    }

    private fun requestPermission() {
        if (ActivityCompat.checkSelfPermission(this@MainActivity,android.Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_DENIED && ActivityCompat.checkSelfPermission(this@MainActivity,android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_DENIED ){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION),1)
        }
        return
    }

    override fun onStart() {
        binding.mapview.onStart()
        MapKitFactory.getInstance().onStart()
        super.onStart()
    }

    override fun onStop() {
        binding.mapview.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onObjectAdded(userLocationView: UserLocationView) {
        locationMapKit.setAnchor(PointF((binding.mapview.width()*0.5).toFloat(),(binding.mapview.height()*0.5).toFloat()), PointF((binding.mapview.width()*0.5).toFloat(),(binding.mapview.height()*0.83).toFloat()))
        userLocationView.arrow.setIcon(ImageProvider.fromResource(this@MainActivity,R.drawable.user_arrow))
        val icon = userLocationView.pin.useCompositeIcon()
        icon.setIcon(ICON, ImageProvider.fromResource(this@MainActivity, R.drawable.search_result), IconStyle().setAnchor(PointF(0f,0f)).setRotationType(RotationType.ROTATE).setZIndex(1f).setScale(0.5f))
        icon.setIcon(PIN, ImageProvider.fromResource(this@MainActivity, R.drawable.nothing), IconStyle().setAnchor(PointF(0.5f,0.5f)).setRotationType(RotationType.ROTATE).setZIndex(1f).setScale(0.5f))
        userLocationView.accuracyCircle.fillColor = Color.BLUE and -0x66000001
    }

    override fun onObjectRemoved(p0: UserLocationView) {}

    override fun onObjectUpdated(p0: UserLocationView, p1: ObjectEvent) {}

    override fun onSearchResponse(response: Response) {
        val mapObjects = binding.mapview.map.mapObjects
        mapObjects.clear()
        response.collection.children.forEach {
            val resultCollection = it.obj?.geometry?.get(0)?.point
            if (resultCollection!=null){
                mapObjects.addPlacemark(resultCollection, ImageProvider.fromResource(this@MainActivity,R.drawable.search_result))
            }
        }
    }

    override fun onSearchError(error: Error) {
        var errorMessage = "Kechirasiz xatolik"
        if (error is RemoteError){
            errorMessage = "Simdagi xatolik"
        }else if (error is NetworkError){
            errorMessage = "Internetdagi xatolik"
        }
        Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
    }

    override fun onCameraPositionChanged(
        map: Map,
        cameraPosition: CameraPosition,
        cameraUpdateReason: CameraUpdateReason,
        finished: Boolean
    ) {
        if (finished){
            submitQuery(binding.search.text.toString())
        }
    }
    companion object{
        const val ICON = "icon"
        const val PIN = "pin"
    }
}