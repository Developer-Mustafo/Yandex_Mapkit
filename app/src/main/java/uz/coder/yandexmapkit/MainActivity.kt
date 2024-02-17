package uz.coder.yandexmapkit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
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
            Animation(Animation.Type.SMOOTH,300f),null
        )
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