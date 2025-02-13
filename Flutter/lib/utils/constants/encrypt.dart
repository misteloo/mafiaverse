String encryptDecrypt(String input) {
  var key = ['a', 'b', 'c'];
  var output = [];

  for (var i = 0; i < input.length; i++) {
    var charCode = input.codeUnitAt(i) ^ key[i % key.length].codeUnitAt(0);
    output.add(String.fromCharCode(charCode));
  }

  return output.join("");
}
