var register = document.getElementById('register');
register.addEventListener('click', function() {
    var password = document.getElementById('password');
    var currentClass = password.className;
    password.className = currentClass === "" ? "password" : "";
});
