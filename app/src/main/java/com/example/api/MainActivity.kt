package com.example.api

import android.content.Context
import android.graphics.Color
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
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapOptions
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.PathOverlay
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : FragmentActivity(), OnMapReadyCallback {
    private lateinit var naverMap: NaverMap

    override fun onCreate(savedInstanceState: Bundle?) {
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

        // naverMap.mapType = NaverMap.MapType.Hybrid // 지도 형태

        naverMap.isIndoorEnabled = true // 실내지도

        // 카메라
//        val cameraPosition = CameraPosition(
//            LatLng(37.5666102, 126.9783881), // 대상 지점
//            16.0, // 줌 레벨
//            20.0, // 기울임 각도
//            180.0 // 베어링 각도
//        )
//        val currentCameraPosition = naverMap.cameraPosition // 카메라의 현재위치 알 수 있음
//        val cameraUpdate = CameraUpdate.scrollTo(LatLng(37.5666102, 126.9783881))
//        naverMap.moveCamera(cameraUpdate) // 카메라 이동
//        // 카메라 영역 제한하기(한반도 인근)
//        naverMap.extent = LatLngBounds(LatLng(31.43, 122.37), LatLng(44.35, 132.0))
//
//        naverMap.addOnOptionChangeListener {
//            // 옵션 변경 이벤트
//            // 지도 유형, 디스플레이 옵션 등 지도와 관련된 옵션이 변경되면 이벤트가 발생합니다.
//        }

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
        Toast.makeText(this, "안녕 ${name}", Toast.LENGTH_LONG).show()

        Toast.makeText(
            this,
            "위도: ${coord.latitude}, 경도: ${coord.longitude}, ${bound.northEast}, ${bounds.northEast}",
            Toast.LENGTH_LONG
        ).show()

        val APIKEY_ID = "tgoutvp62u"
        val APIKEY = "sVfCuiLh1aK2gLTNqEPPn24P5r7gybDHLVEyVibx"
        //레트로핏 객체 생성
        val retrofit = Retrofit.Builder()
            .baseUrl("https://naveropenapi.apigw.ntruss.com/map-direction/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(NaverAPI::class.java)

        //근처에서 길찾기
        val callgetPath = api.getPath(APIKEY_ID, APIKEY, "127.13602285714192 , 35.840335812433025", "127.12944193975801, 35.84678030608311")

        callgetPath.enqueue(object : Callback<ResultPath> { // 비동기 방식으로 API 요청
            override fun onResponse(call: Call<ResultPath>, response: Response<ResultPath>) { // Response 객체를 통해 응답 데이터를 접근가능
                val path_cords_list = response.body()?.route?.traoptimal // traoptimal = 실시간 최적경로 넣기

                val path = PathOverlay() // 경로선을 나타내는 위한 오버레이

                // 경로 넣어놓기 위한 공간, MutableList에 add 기능 쓰기 위해 더미 원소(0.1, 0.1) 하나 넣어둠
                val path_container: MutableList<LatLng> = mutableListOf(LatLng(0.1, 0.1))

                //경로 그리기 응답바디가 List<List<Double>> 이라서 2중 for문 썼음
                //구한 경로를 하나씩 path_container에 추가
                path_cords_list?.forEach { path_cords ->
                    path_cords.path.forEach { path_cords_xy ->
                        path_container.add(LatLng(path_cords_xy[1], path_cords_xy[0]))
                    }
                }

                //더미원소(0.1,0.1) 드랍후 path.coords에 path들을 넣어줌.
                path.coords = path_container.drop(1)
                path.color = Color.GREEN
                path.map = naverMap // 경로선 그리기

                //경로 시작점으로 화면 이동
                if (path.coords.isNotEmpty()) {
                    val cameraUpdate = CameraUpdate.scrollTo(path.coords[0])
                        .animate(CameraAnimation.Fly, 3000)
                    naverMap.moveCamera(cameraUpdate)

                    Toast.makeText(this@MainActivity, "경로 안내가 시작됩니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResultPath>, t: Throwable) {
                Toast.makeText(this@MainActivity, "경로 안내 실패: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
