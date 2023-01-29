<!DOCTYPE html>
<html lang="en">
<head>
    <title>IT-ndr</title>
</head>
<body>
<h1>IT-ndr</h1>
<p>
    <a href="/">Home</a>
</p>
<p>
<form action="/${id}" method="post">
    <label for="demand">Demand</label>
    <input name="demand" type="number" <#if demandFilled>disabled</#if>/>
    <label for="offer">Offer</label>
    <input name="offer" type="number" <#if offerFilled>disabled</#if>/>
    <input value="Save" type="submit"/>
</form>
</p>
</body>
</html>

