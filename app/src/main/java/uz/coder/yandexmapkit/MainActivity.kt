package uz.coder.yandexmapkit

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.runtime.bindings.Archive
import com.yandex.runtime.bindings.internal.ArchiveWriter
import uz.coder.yandexmapkit.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
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
        val locationLayer = instance.createUserLocationLayer(binding.mapview.mapWindow)
        locationLayer.isVisible = true
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
}