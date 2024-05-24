package com.example.api

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentActivity
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapOptions
import com.naver.maps.map.OnMapReadyCallback

class MainActivity : FragmentActivity(), OnMapReadyCallback {
    private lateinit var naverMap: NaverMap


    override fun onCreate(savedInstanceState: Bundle?) {

//        // 카메라 시작 지점 -> 실패
//        val options = NaverMapOptions()
//            .camera(CameraPosition(LatLng(35.1798159, 129.0750222), 8.0))
//            .mapType(NaverMap.MapType.Terrain)
//        val mapFragment1 = MapFragment.newInstance(options)
//        mapFragment1.getMapAsync(this)


        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 지도 만들기
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map, it).commit()
            } 
        mapFragment.getMapAsync(this) // onMapReady 기다리기
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap // naverMap 변수 초기화

        //naverMap.mapType = NaverMap.MapType.Hybrid // 지도 형태

        naverMap.isIndoorEnabled = true // 실내지도

        // 카메라
        val cameraPosition = CameraPosition(
            LatLng(37.5666102, 126.9783881), // 대상 지점
            16.0, // 줌 레벨
            20.0, // 기울임 각도
            180.0 // 베어링 각도
        )
        val currentCameraPosition = naverMap.cameraPosition // 카메라의 현재위치 알 수 있음
        val cameraUpdate = CameraUpdate.scrollTo(LatLng(37.5666102, 126.9783881))
        naverMap.moveCamera(cameraUpdate) // 카메라 이동
        // 카메라 영역 제한하기(한반도 인근)
        naverMap.extent = LatLngBounds(LatLng(31.43, 122.37), LatLng(44.35, 132.0))

        naverMap.addOnOptionChangeListener {
            // 옵션 변경 이벤트
            // 지도 유형, 디스플레이 옵션 등 지도와 관련된 옵션이 변경되면 이벤트가 발생합니다.
        }


        // UI
        val uiSettings = naverMap.uiSettings // 객체 접근
        uiSettings.isCompassEnabled = true // 나침반 활성화
        uiSettings.isLocationButtonEnabled = true // 현재위치 버튼 활성화, 단 기능은 없음




        val coord = LatLng(37.5670135, 126.9783740)

        val southWest = LatLng(31.43, 122.37)
        val northEast = LatLng(44.35, 132.0)

        val bound = LatLngBounds(southWest, northEast)

        val bounds = LatLngBounds.Builder()
            .include(LatLng(37.5640984, 126.9712268))
            .include(LatLng(37.5651279, 126.9767904))
            .include(LatLng(37.5625365, 126.9832241))
            .include(LatLng(37.5585305, 126.9809297))
            .include(LatLng(37.5590777, 126.974617))
            .build()

        val name = "jaewoo"
        Toast.makeText(this,
            "안녕 ${name}",
            Toast.LENGTH_LONG).show()

        Toast.makeText(this,
            "위도: ${coord.latitude}, 경도: ${coord.longitude}, ${bound.northEast}, ${bounds.northEast}",
            Toast.LENGTH_LONG).show()



        }

}