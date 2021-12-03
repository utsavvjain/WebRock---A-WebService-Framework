class Student
{
constructor(rollNumber,name,gender){
this.rollNumber=rollNumber;
this.name=name;
this.gender=gender;
}
getRollNumber(){
return this.rollNumber;
}
getName(){
return this.name;
}
getGender(){
return this.gender;
}
}
class StudentService
{
constructor(){
}
add(args0){
return new Promise((resolve,reject)=>{
fetch(`test/students/add`,{method : 'POST',headers : {'Content-Type' : 'application/json'},body : JSON.stringify(args0)}).then(response=>response.json()).then(response=>resolve(response)).catch(error=>reject(error));
})
}
get(rollNumber){
return new Promise((resolve,reject)=>{
fetch(`test/students/get?rollNumber=${encodeURI(rollNumber)}`).then(response=>response.json()).then(response=>resolve(response)).catch(error=>reject(error));
})
}
update(args0){
return new Promise((resolve,reject)=>{
fetch(`test/students/update`,{method : 'POST',headers : {'Content-Type' : 'application/json'},body : JSON.stringify(args0)}).then(response=>response.json()).then(response=>resolve(response)).catch(error=>reject(error));
})
}
delete(rollNumber){
return new Promise((resolve,reject)=>{
fetch(`test/students/delete`,{method : 'POST',headers : {'Content-Type' : 'application/x-www-form-urlencoded'},body : `rollNumber=${encodeURI(rollNumber)}`}).then(response=>response.json()).then(response=>resolve(response)).catch(error=>reject(error));
})
}
getAll(){
return new Promise((resolve,reject)=>{
fetch(`test/students/all?`).then(response=>response.json()).then(response=>resolve(response)).catch(error=>reject(error));
})
}
}
