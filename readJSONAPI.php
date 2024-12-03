<?php
// Tạo kết nối đến cơ sở dữ liệu
$con = mysqli_connect('localhost', 'root', 'Nam20022003@', 'testcsdl');
if (mysqli_connect_errno()) {
    // Kiểm tra kết nối và báo lỗi nếu không thành công
    die("Kết nối thất bại: " . mysqli_connect_error());
}
mysqli_query($con, "SET NAMES 'utf8'"); // Thiết lập charset utf8

// Kiểm tra nếu yêu cầu là phương thức POST
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    // Kiểm tra xem các tham số 'username', 'password', và 'action' có được gửi không
    if (isset($_POST['username']) && isset($_POST['password']) && isset($_POST['action'])) {
        // Lấy giá trị của các tham số
        $username = $_POST['username'];  // Lấy username từ yêu cầu POST
        $password = $_POST['password'];  // Lấy password từ yêu cầu POST
        $action = $_POST['action'];      // Loại hành động (đăng nhập hoặc đăng ký)

        // Đăng nhập
        if ($action == "login") {
            // Giả sử $password là mật khẩu người dùng nhập vào
            $query = "SELECT * FROM user WHERE username = ?";
            $stmt = $con->prepare($query);
            $stmt->bind_param('s', $username);
            $stmt->execute();
            $result = $stmt->get_result();

            if (mysqli_num_rows($result) > 0) {
                $row = mysqli_fetch_assoc($result);
                $db_password = $row['password']; // Mật khẩu từ cơ sở dữ liệu

                if (password_verify($password, $db_password)) {
                    // Nếu mật khẩu đúng
                    $response = array("status" => "success", "message" => "Đăng nhập thành công");
                } else {
                    // Nếu mật khẩu sai
                    $response = array("status" => "error", "message" => "Mật khẩu không chính xác");
                }
                
            } else {
                $response = array("status" => "error", "message" => "Tên đăng nhập không tồn tại");
            }

        } elseif ($action == "register") {
            // Đăng ký
            $query = "SELECT * FROM user WHERE username = ?";  // Thay đổi bảng thành 'user'
            $stmt = $con->prepare($query);
            $stmt->bind_param('s', $username);
            $stmt->execute();
            $result = $stmt->get_result();

            if (mysqli_num_rows($result) > 0) {
                // Nếu username đã tồn tại
                $response = array("status" => "error", "message" => "Tên đăng nhập đã tồn tại.");
            } else {
                // Mã hóa mật khẩu trước khi lưu vào cơ sở dữ liệu
                $hashedPassword = password_hash($password, PASSWORD_DEFAULT);
                $insertQuery = "INSERT INTO user (username, password) VALUES (?, ?)";  // Thay đổi bảng thành 'user'
                $stmt = $con->prepare($insertQuery);
                $stmt->bind_param('ss', $username, $hashedPassword);

                if ($stmt->execute()) {
                    // Đăng ký thành công
                    $response = array("status" => "success", "message" => "Đăng ký thành công!");
                } else {
                    // Lỗi khi thêm người dùng mới
                    $response = array("status" => "error", "message" => "Đăng ký thất bại.");
                }
            }

        } else {
            // Hành động không hợp lệ
            $response = array("status" => "error", "message" => "Hành động không hợp lệ.");
        }

        // Trả về kết quả dưới dạng JSON
        echo json_encode($response, JSON_UNESCAPED_UNICODE);

    } else {
        // Nếu thiếu tham số
        $response = array("status" => "error", "message" => "Thiếu tham số");
        echo json_encode($response, JSON_UNESCAPED_UNICODE);
    }
} 

// Nếu không có tham số đăng nhập hoặc đăng ký, lấy danh sách sản phẩm
if (!isset($_POST['username']) && !isset($_POST['password']) && !isset($_POST['action'])) {
    $result = mysqli_query($con, 'SELECT * FROM product');  // Lấy dữ liệu sản phẩm từ cơ sở dữ liệu

    if ($result) {
        $col = [];
        while ($row = mysqli_fetch_array($result)) {
            $col[] = $row;  // Thêm dữ liệu sản phẩm vào mảng
        }
        // Trả về dữ liệu sản phẩm dưới dạng JSON
        echo json_encode($col, JSON_UNESCAPED_UNICODE);
    } else {
        // Nếu không có sản phẩm, trả về một mảng rỗng
        echo json_encode(array(), JSON_UNESCAPED_UNICODE);
    }
}

// Đóng kết nối cơ sở dữ liệu
mysqli_close($con);
?>
