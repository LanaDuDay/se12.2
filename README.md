# se12.2
Xây dựng AppAndroidStudio:

1. Phần Đăng nhập người dùng:
File Application 3 sẽ là file Project Của Android Studio đảm nhận chức năng đăng nhập và đăng ký người dùng.
Đầu tiên người dùng chọn đăng ký tài khoản, nếu tài khoản đã được đăng ký thì chuyển hướng sang nút bấm login,
Sau khi đăng nhập người dùng, ứng dụng sẽ chuyển sang Activity mới thực hiện chức năng xem thông tin người dùng,.:
Sau đây là tên các file .java tương ứng với từng Activity. Mỗi file .java sẽ đi kèm với một file .xml cùng tên:
- Login.java:


Đây là mã nguồn Java cho một ứng dụng Android đăng nhập sử dụng Firebase Authentication. Mã này bao gồm một Activity (Login) để xử lý quá trình đăng nhập người dùng vào ứng dụng.

Mô tả ngắn gọn của các chức năng chính:

Layout XML: Sử dụng file activity_login.xml để hiển thị giao diện đăng nhập với hai trường nhập liệu (email và password), các nút và thanh tiến trình (progress bar).

Firebase Authentication: Sử dụng Firebase Authentication để xác thực người dùng thông qua địa chỉ email và mật khẩu.

Chức năng đăng nhập:

Người dùng nhập email và mật khẩu vào các trường tương ứng.
Kiểm tra xem các trường đó có trống không và hiển thị thông báo lỗi nếu cần.
Nếu không có lỗi, sử dụng phương thức signInWithEmailAndPassword để thực hiện quá trình đăng nhập.
Xử lý các kết quả: nếu đăng nhập thành công, chuyển hướng sang màn hình UserProfileActivity. Nếu không, hiển thị thông báo lỗi cụ thể.
Xử lý lỗi đăng nhập: Bắt các loại lỗi khác nhau mà Firebase Authentication có thể trả về (ví dụ: email không tồn tại, mật khẩu không đúng) và hiển thị thông báo lỗi tương ứng để người dùng biết lý do đăng nhập thất bại.

Chuyển hướng người dùng: Nếu người dùng đã đăng nhập trước đó và vẫn còn phiên đăng nhập hiện tại, họ sẽ được chuyển hướng trực tiếp đến màn hình UserProfileActivity.

Để viết một báo cáo readme ngắn gọn, bạn có thể mô tả mã nguồn trong file này như sau:

Mô tả:

Tên File: Login.java
Chức năng: Xác thực người dùng thông qua Firebase Authentication và chuyển hướng đến màn hình người dùng nếu đăng nhập thành công.
Các Chức Năng Chính:
Xử lý đăng nhập người dùng sử dụng email và mật khẩu.
Hiển thị thông báo lỗi khi xảy ra vấn đề trong quá trình đăng nhập.
Chuyển hướng người dùng đến màn hình người dùng khi đăng nhập thành công.
Công Nghệ Sử Dụng: Firebase Authentication, Android SDK.

Register.java:
Tương tự như mã nguồn trước đó, mã nguồn này là một Activity (Register) của ứng dụng Android sử dụng Firebase Authentication để đăng ký người dùng mới. Dưới đây là mô tả ngắn gọn về các chức năng chính trong mã nguồn này:

Mô tả:

Tên File: Register.java

Chức năng: Đăng ký người dùng mới thông qua Firebase Authentication và lưu thông tin người dùng vào Firebase Realtime Database.

Các Chức Năng Chính:

Người dùng nhập thông tin đăng ký như email, mật khẩu, tên đầy đủ, ngày sinh và số điện thoại.
Kiểm tra tính hợp lệ của thông tin đăng ký: email, mật khẩu, định dạng email, độ dài mật khẩu, và số điện thoại.
Sử dụng createUserWithEmailAndPassword để tạo người dùng mới trên Firebase Authentication.
Lưu thông tin người dùng (tên, ngày sinh, số điện thoại) vào Firebase Realtime Database sau khi đăng ký thành công.
Xử lý các lỗi phát sinh trong quá trình đăng ký và hiển thị thông báo lỗi cụ thể cho người dùng.
Công Nghệ Sử Dụng: Firebase Authentication, Firebase Realtime Database, Android SDK.

Để viết một báo cáo README ngắn gọn, bạn có thể sử dụng các điểm sau:

Mô tả file mã nguồn: Register.java là một Activity trong ứng dụng Android thực hiện chức năng đăng ký người dùng mới.
Chức năng chính: Xác thực người dùng mới thông qua Firebase Authentication và lưu thông tin người dùng vào Firebase Realtime Database.
Các bước thực hiện:
Người dùng nhập thông tin đăng ký vào các trường tương ứng.
Kiểm tra tính hợp lệ của thông tin và hiển thị thông báo lỗi nếu có.
Thực hiện đăng ký thông qua Firebase Authentication và lưu thông tin người dùng vào Realtime Database.
Xử lý các lỗi xác thực và hiển thị thông báo lỗi cụ thể cho người dùng.
Công nghệ và công cụ sử dụng: Firebase Authentication, Firebase Realtime Database, Android SDK.
UserProfileActivity.java:
Tên File: UserProfileActivity.java
Chức năng: Hiển thị thông tin người dùng và cung cấp các tùy chọn liên quan đến quản lý hồ sơ người dùng.
Các Chức Năng Chính:
Kiểm tra trạng thái đăng nhập của người dùng. Nếu chưa đăng nhập, chuyển hướng đến màn hình đăng nhập (Login).
Hiển thị thông tin người dùng từ Firebase Realtime Database.
Cung cấp các tùy chọn như cập nhật hồ sơ, đăng xuất thông qua ActionBar Menu.
Công Nghệ và Công Cụ Sử Dụng: Firebase Authentication, Firebase Realtime Database, Android SDK.
Để viết README ngắn gọn, bạn có thể sử dụng các điểm sau:

Mô tả: UserProfileActivity.java là một Activity trong ứng dụng Android để hiển thị thông tin người dùng và quản lý hồ sơ.
Chức năng chính:
Kiểm tra trạng thái đăng nhập của người dùng và hiển thị thông tin từ Firebase Realtime Database.
Cung cấp các tùy chọn như cập nhật hồ sơ, đăng xuất thông qua ActionBar Menu.
Các bước thực hiện:
Kiểm tra trạng thái đăng nhập, nếu chưa đăng nhập, chuyển hướng đến màn hình đăng nhập (Login).
Hiển thị thông tin người dùng từ Firebase Realtime Database.
Xử lý các tùy chọn từ ActionBar Menu như cập nhật hồ sơ, đăng xuất và các tính năng khác.
Công nghệ và công cụ sử dụng: Firebase Authentication, Firebase Realtime Database, Android SDK.
UpdateUserProfileActivity:
Mô tả: UpdateProfileActivity.java là một Activity trong ứng dụng Android, cho phép người dùng cập nhật thông tin hồ sơ.
Chức năng chính:
Hiển thị thông tin hồ sơ người dùng từ Firebase Realtime Database.
Cho phép cập nhật tên, ngày sinh và số điện thoại.
Cập nhật thông tin người dùng lên Firebase Realtime Database và Firebase Authentication.
Các bước thực hiện:
Hiển thị thông tin hồ sơ người dùng hiện tại từ Firebase Realtime Database.
Cho phép người dùng cập nhật thông tin và lưu vào Firebase Realtime Database.
Công nghệ và công cụ sử dụng: Firebase Authentication, Firebase Realtime Database, Android SDK.




Phần 2: Chức năng chính của sàn giao dịch

Main.py đóng vai trò là một server trên flask, khi run file main.py thì cần thay đổi địa chỉ url của các file java trong RecycleView.

File RecycleView sẽ thực hiện các chức năng tương đối giống figma.

Mainactivity.java: sẽ thực hiện in ra các recycleView bao gồm các token đang sở hữu, giá token, cũng như là orderBook.
Import Thư Viện: Tệp này import các thư viện cần thiết cho Android và Retrofit, một thư viện giúp thực hiện các yêu cầu HTTP.

Thiết Lập Activity: Lớp MainActivity mở rộng từ AppCompatActivity. Phương thức onCreate được sử dụng để thiết lập giao diện từ tệp XML (activity_main) và khởi tạo các thành phần khác nhau.

Lắng Nghe Sự Kiện Nút: Phương thức setupButtonClickListeners được sử dụng để lắng nghe sự kiện click cho một số ImageView, kích hoạt các hiệu ứng và mở các activity khác nhau khi được click.

Lấy Dữ Liệu và Cập Nhật Giao Diện: Phương thức fetchDataAndUpdateUI sử dụng Retrofit để thực hiện yêu cầu mạng không đồng bộ để lấy dữ liệu thị trường. Sau khi nhận được phản hồi, dữ liệu được xử lý và nếu thành công, giao diện người dùng được cập nhật.

Handler Cho Cập Nhật Định Kỳ: Có một Handler được sử dụng để lên lịch cập nhật định kỳ, lấy dữ liệu và cập nhật giao diện mỗi 60 giây.

ViewPropertyAnimator Cho ImageView: Các ImageView có lắng nghe sự kiện click với hiệu ứng animation sử dụng ViewPropertyAnimator.

Dialog Order Book: Khi một mục trong RecyclerView được click, một hộp thoại (orderbook) xuất hiện với thông tin bổ sung được lấy từ một điểm cuối API khác.

Dọn Dẹp Khi Hủy Bỏ: Phương thức onDestroy đảm bảo rằng các cập nhật định kỳ được dừng khi activity bị hủy bỏ.

Lưu ý rằng xử lý lỗi cho các yêu cầu mạng được thực hiện cả trong các phương thức onResponse và onFailure của Retrofit. Ngoài ra, mã nguồn bao gồm cập nhật UI trên luồng chính sử dụng runOnUiThread khi xử lý phản hồi mạng.

Mainactivity2.java: In ra lịch sử giao dịch
Mainactivity3.java: 
Import Thư Viện: Tệp này import các thư viện cần thiết cho Android và Retrofit, một thư viện giúp thực hiện các yêu cầu HTTP.

Thiết Lập Activity: Lớp MainActivity mở rộng từ AppCompatActivity. Phương thức onCreate được sử dụng để thiết lập giao diện từ tệp XML (activity_main) và khởi tạo các thành phần khác nhau.

Lắng Nghe Sự Kiện Nút: Phương thức setupButtonClickListeners được sử dụng để lắng nghe sự kiện click cho một số ImageView, kích hoạt các hiệu ứng và mở các activity khác nhau khi được click.

Lấy Dữ Liệu và Cập Nhật Giao Diện: Phương thức fetchDataAndUpdateUI sử dụng Retrofit để thực hiện yêu cầu mạng không đồng bộ để lấy dữ liệu thị trường. Sau khi nhận được phản hồi, dữ liệu được xử lý và nếu thành công, giao diện người dùng được cập nhật.

Handler Cho Cập Nhật Định Kỳ: Có một Handler được sử dụng để lên lịch cập nhật định kỳ, lấy dữ liệu và cập nhật giao diện mỗi 60 giây.

ViewPropertyAnimator Cho ImageView: Các ImageView có lắng nghe sự kiện click với hiệu ứng animation sử dụng ViewPropertyAnimator.

Dialog Order Book: Khi một mục trong RecyclerView được click, một hộp thoại (orderbook) xuất hiện với thông tin bổ sung được lấy từ một điểm cuối API khác.

Dọn Dẹp Khi Hủy Bỏ: Phương thức onDestroy đảm bảo rằng các cập nhật định kỳ được dừng khi activity bị hủy bỏ.

Lưu ý rằng xử lý lỗi cho các yêu cầu mạng được thực hiện cả trong các phương thức onResponse và onFailure của Retrofit. Ngoài ra, mã nguồn bao gồm cập nhật UI trên luồng chính sử dụng runOnUiThread khi xử lý phản hồi mạng.



