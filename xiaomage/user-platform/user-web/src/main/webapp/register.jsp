<c:set var="ctx" value="<%=request.getContextPath()%>" />
<head>
<jsp:directive.include file="/WEB-INF/jsp/prelude/include-head-meta.jspf" />
	<title>My Home Page</title>
    <style>
      .bd-placeholder-img {
        font-size: 1.125rem;
        text-anchor: middle;
        -webkit-user-select: none;
        -moz-user-select: none;
        -ms-user-select: none;
        user-select: none;
      }
      @media (min-width: 768px) {
        .bd-placeholder-img-lg {
			font-size: 3.5rem;
        }
      }
    </style>
</head>
<body>
	<div class="container">
		<form class="form-register" method="post" content="text/html; charset=UTF-8" action="${ctx}/user/register">
			<h1 class="h3 mb-3 font-weight-normal">
				注册
			</h1>
			<label for="inputName" class="sr-only">
				请输入用户名
			</label>
			<input type="text" id="inputName" name="name" class="form-control" placeholder="请输入用户名" required>
			<label for="inputPassword" class="sr-only">
				请输入密码
			</label>
			<input type="password" id="inputPassword" name="password" class="form-control" placeholder="请输入密码" required>
			<label for="inputEmail" class="sr-only">
				请输入电子邮件
			</label>
			<input type="email" id="inputEmail" name="email" class="form-control" placeholder="请输入电子邮件" required autofocus>
			<label for="inputPhoneNumber" class="sr-only">
				请输入手机号
			</label>
			<input type="text" id="inputPhoneNumber" name="phoneNumber" class="form-control" placeholder="请输入手机号" required>
			<button class="btn btn-lg btn-primary btn-block" type="submit">
				Register
			</button>
			<p class="mt-5 mb-3 text-muted">&copy; 2017-2021</p>
		</form>
	</div>
</body>