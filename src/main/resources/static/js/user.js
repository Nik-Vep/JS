let currentUser = "";

fetch("/api/user/").then(res => res.json())
    .then(data => {
        currentUser = data;
        console.log(data)
        showOneUser(currentUser);
        showUserRoles(currentUser);
        showUserEmail(currentUser);
    })

function showOneUser(user) {
    let temp = "";
    temp += "<tr>"
    temp += "<td>" + user.id + "</td>"
    temp += "<td>" + user.username + "</td>"
    temp += "<td>" + user.surname + "</td>"
    temp += "<td>" + user.department + "</td>"
    temp += "<td>" + user.salary + "</td>"
    temp += "</tr>"
    document.getElementById("oneUserBody").innerHTML = temp;
}

function showUserRoles(user) {
    let roles = user.roles.map(role => role.authority).join(", ");
    document.getElementById("userRoles").innerText = roles;}

function showUserEmail(user) {
    let email = user.email;
    document.getElementById("userEmail").innerText = email;}

